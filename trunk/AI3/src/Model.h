/*
 * Model.h
 *
 *  Created on: Feb 10, 2010
 *      Author: jsandbrook
 */

#ifndef MODEL_H_
#define MODEL_H_

// Includes
#include <cstdio>
#include <iostream>

// Structs
struct charData {
	charData *next, *prev;// Linked list

	char character;
	int frequency;
};

struct bigram {
	bigram *next, *prev;// Linked list

	char firstChar;
	charData *predictions;
};

struct trigram {
	trigram *next, *prev;// Linked list

	char firstChar, secondChar;
	charData *predictions;
};

/*
 * Model Class
 * 	Represents a test and train pair of inputs. The files used to create a model are sequences of single
 * 	characters separated by commas.
 */
class Model {
public:
	Model(char*, char*);
	virtual ~Model();
	bool readTestFile(char*);
	bool readTrainFile(char*);
	void printModel();
	void analyzeUnigram();
	void analyzeBigram();
	void analyzeTrigram();
	char predict();

private:
	charData *firstChar;
	bigram *firstBi;
	trigram *firstTri;
	int *testArray, testLength;
	int *trainArray, trainLength;

	charData* allocateCharData();
	bigram* allocateBigram();
	trigram* allocateTrigram();
	charData* sortCharData(charData*);
};

#endif /* MODEL_H_ */
