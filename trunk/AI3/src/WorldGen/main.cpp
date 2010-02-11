/*
 * main.cpp
 *
 *  Created on: Feb 11, 2010
 *      Author: jsandbrook
 */

#include <iostream>
#include <cstdlib>

int main(int argc, char** argv) {
	int numChars;
	char cur;
	FILE* hFile;

	if(argc != 3) {
		std::cout << "Incorrect arguments.\nUsage main.exe <filename> <numChars>\n";
		return 0;
	}

	numChars = atoi(argv[2]);

	// Open the file, ignoring any files with the same name
	hFile = fopen(argv[1], "w");

	std::cout << "Generating " << numChars << " characters into file " << argv[1] << "\n";

	srand(time(NULL));
	for(int i = 0; i < numChars; i++) {
		cur = (char)(rand() % 26) + 65;

		fwrite(&cur, 1, 1, hFile);

		if(i != numChars - 1) {
			fwrite(",", 1, 1, hFile);
		}
	}
}
