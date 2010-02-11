/*
 * main.cpp
 *
 *  Created on: Feb 10, 2010
 *      Author: jsandbrook
 */

#include "main.h"

int main(int argc, char** argv) {
	Model* model;

	// Read the command line arguments
	if(argc != 3) {
		std::cout << "Incorrect arguments.\nUsage: main.exe <testfilename> <trainfilename>";
	}

	// Create the model
	model = new Model(argv[1], argv[2]);
	model->printModel();

	return 0;
}


