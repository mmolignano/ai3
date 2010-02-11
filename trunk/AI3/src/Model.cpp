/*
 * Model.cpp
 *
 *  Created on: Feb 10, 2010
 *      Author: jsandbrook
 */

#include "Model.h"

Model::Model(char* test, char* train) {
	readTestFile(test);
	readTrainFile(train);
}

Model::~Model() {

}

/*
 * Reads in a file of single characters separated by commas. It ignores new line characters.
 */
bool Model::readTestFile(char* file) {
	FILE* hFile;
	std::cout << "Reading test file\n";
	int ret = 0, len = 1, i = 0;

	// Open the file
	hFile = fopen(file, "r");
	if(hFile == NULL) {
		std::cout << "Error opening file\n";
		return false;
	}

	// Find how many characters there are
	while(ret != EOF) {
		ret = fgetc(hFile);
		if(ret == 44) {// If there's a comma
			len++;
		}
	}
	std::cout << "Found " << len << " characters\n";

	freopen(file, "r", hFile); // Reopen the file to start from the beginning
	testArray = (int*)calloc(len, sizeof(int));
	testLength = len;

	// Fill in the array of characters
	std::cout << "Filling array\n";
	ret = fgetc(hFile);
	while(ret != EOF) {
		if(ret != 44) {
			testArray[i] = ret;
			i++;
		}

		ret = fgetc(hFile);
	}

	return true;
}

/*
 * Reads in a file of single characters separated by commas. It ignores new line characters.
 */
bool Model::readTrainFile(char* file) {
	FILE* hFile;
	int ret = 0, len = 1, i = 0;

	// Open the file
	hFile = fopen(file, "r");
	if(hFile == NULL) {
		std::cout << "Error opening file\n";
		return false;
	}

	// Find how many characters there are
	while(ret != EOF) {
		ret = fgetc(hFile);
		if(ret == 44) {// If there's a comma
			len++;
		}
	}

	freopen(file, "r", hFile); // Reopen the file to start from the beginning
	trainArray = (int*)calloc(len, sizeof(int));
	trainLength = len;

	// Fill in the array of characters
	ret = fgetc(hFile);
	while(ret != EOF) {
		if(ret != 44) {
			trainArray[i] = ret;
			i++;
		}

		ret = fgetc(hFile);
	}

	return true;
}

/*
 * Prints the currently read files
 */
void Model::printModel() {
	std::cout << "Training data:\n";
	for(int i = 0; i < trainLength; i++) {
		std::cout << trainArray[i] << ",";
	}
	std::cout << "\nTest data:\n";
	for(int i = 0; i < testLength; i++) {
		std::cout << testArray[i] << ",";
	}
	std::cout << "\n\n";

	analyzeUnigram();
	charData* cur;
	cur = firstChar;
	while(cur != NULL) {
		std::cout << cur->character << "  :  " << cur->frequency << "\n";
		cur = cur->next;
	}

	analyzeBigram();
	bigram* curBi;
	charData* charBi;
	curBi = firstBi;
	while(curBi != NULL) {
		charBi = curBi->predictions;
		while(charBi != NULL) {
			std::cout << charBi->character << " | " << curBi->firstChar << " : " << charBi->frequency << "\n";
			charBi = charBi->next;
		}
		curBi = curBi->next;
	}
}

/*
 * Analyzes the training data according to a unigram model
 */
void Model::analyzeUnigram() {
	charData* cur;
	char first = 0, second = 0;

	// Clear any current charData info and allocate data
	/* TODO: Iterate through list and actually delete stuff */
	firstChar = NULL;
	firstChar = allocateCharData();

	// Step through each character in the file and update our info
	for(int i = 0; i < trainLength; i++) {
		// Update for unigram
		cur = firstChar;
		while(cur != NULL) {
			if(cur->character == trainArray[i]) {// Update the frequency of the correct charData struct
				cur->frequency++;
				break;// We don't need to check the rest
			} else if(cur->character == 0) {// This is the first iteration
				cur->character = trainArray[i];
				cur->frequency = 1;
			} else if(cur->next == NULL) {// Allocate a new charData struct
				cur->next = allocateCharData();
				cur->next->character = trainArray[i];
				cur->next->frequency = 1;
				cur->next->prev = cur;
				break;
			}

			cur = cur->next;
		}
	}

	// Sort the unigram data by most frequent
	firstChar = sortCharData(firstChar);
}

/*
 * Analyzes read data according to a bigram model
 */
void Model::analyzeBigram() {
	charData *curChar;
	bigram *cur;
	char known = 0;

	// Clear out old bigram info and allocate new space
	/* TODO: Actually delete any old bigram info */
	firstBi = NULL;
	firstBi = allocateBigram();

	// Step through each character in the file and update our info
	known = trainArray[0];
	for(int i = 1; i < trainLength; i++) {
		cur = firstBi;

		while(cur != NULL) {// Loop through the bigram structs
			if(cur->firstChar == known) {// If we find a matching known character
				curChar = cur->predictions;
				while(curChar != NULL) {// Loop through this known character's predictions
					if(curChar->character == trainArray[i]) {
						curChar->frequency++;
						break;// No need to check the others
					} else if(curChar->next == NULL) {// No more predictions for this known character, this one's new
						curChar->next = allocateCharData();
						curChar->next->character = trainArray[i];
						curChar->next->prev = curChar;
						curChar->next->frequency++;
						break;
					} else {
						curChar = curChar->next;
					}
				}
				break; // Don't need to check the rest of the bigrams
			} else if(cur->firstChar == 0) {// This is the first iteration
				cur->firstChar = known;
				cur->predictions = allocateCharData();
				cur->predictions->character = trainArray[i];
				cur->predictions->frequency++;
				break;//Continue to the next character
			} else if(cur->next == NULL) {// If we didn't find a matching known character and we're at the end of the list, create a new bigram
				cur->next = allocateBigram();
				cur->next->firstChar = known;
				cur->next->prev = cur;
				cur->next->predictions = allocateCharData();
				cur->next->predictions->character = trainArray[i];
				cur->next->predictions->frequency++;
				break;
			} else {// Else move on to the next stuct
				cur = cur->next;
			}
		}

		known = trainArray[i];
	}

	// Sort each bigrams charData lists
	cur = firstBi;
	while(cur != NULL) {
		cur->predictions = sortCharData(cur->predictions);
		cur = cur->next;
	}
}

/*
 * Allocates memory for a charData structure
 */
charData* Model::allocateCharData() {
	charData *ret;

	ret = (charData*)malloc(sizeof(charData));
	ret->character = 0;
	ret->prev = NULL;
	ret->next = NULL;
	ret->frequency = 0;

	return ret;
}

/*
 * Allocates memory for a bigram structure
 */
bigram* Model::allocateBigram() {
	bigram* ret;

	ret = (bigram*)malloc(sizeof(bigram));
	ret->firstChar = 0;
	ret->next = NULL;
	ret->prev = NULL;
	ret->predictions = NULL;

	return ret;
}

/*
 * Allocates memory for a trigram structure
 */
trigram* Model::allocateTrigram() {
	trigram* ret;

	ret = (trigram*)malloc(sizeof(trigram));
	ret->secondChar = 0;
	ret->firstChar = 0;
	ret->next = NULL;
	ret->prev = NULL;
	ret->predictions = NULL;

	return ret;
}

/*
 * Sorts a linked list of charData structs into descending order by their frequency
 */
charData* Model::sortCharData(charData* first) {
	bool sorted = false;
	int len = 0;
	charData *cur;

	// Determine the list length
	cur = first;
	while(cur != NULL) {
		len++;
		cur = cur->next;
	}

	//  No need to sort if there's only one entry
	if(len == 1) {
		return first;
	}

	// Sort the list
	cur = first;
	while(sorted == false) {
		sorted = true;
		for(int i = 0; i < len - 1; i++) {
			if(cur->next->frequency > cur->frequency) {
				sorted = false;
				cur->next->prev = cur->prev;
				if(cur->prev != NULL) {
					cur->prev->next = cur->next;
				}
				cur->prev = cur->next;
				if(cur->next->next != NULL) {
					cur->next = cur->next->next;
				} else {
					cur->next = NULL;
				}
				cur->prev->next = cur;
				if(cur->next != NULL) {
					cur->next->prev = cur;
				}
			} else {
				cur = cur->next;
			}
		}

		// Find the new first struct
		while(cur->prev != NULL) {
			cur = cur->prev;
		}
	}

	return cur;
}

/*
 * Predicts the next letter in the test file
 */
char Model::predict() {

}
