Module2
=======

This repository contains Module2 files

The folders are divided as following:
eVillain - contains android files
DE2_side - contains DE2 files.


How to use GitHub:

1. Create branch.
2. clone repository to your computer
3. GitShell commands:
	switch branches: git checkout -b [branch_name]
	e.g. initially I am in [master]. I want to switch to branch_kris. Command looks like: git checkout -b branch_kris
	
	git pull origin master - pulls code from MASTER branch into your branch
	git pull - pulls code from branch you're currently on
	
	git status - chack current local repository status (on your workspace)
	git add - add modified files to commit queue
	git add -A - add ALL modified files to commit queue
	git commit - commit your files from workspace to local repository (to sync later)
	
	git push - push to your branch on the server from local repository
	
	FOR MERGING CODE GO TO GITHUB WEBSITE:
	1) switch to your branch
	2) select (on the right side of the table) pull requests
	3) create pull requests
	
	Scenario example 1:
	Tommy merged new code to MASTER. I want to work with latest code
	
	1) I add, commit, and push my own changes up to now to my own branch
	2) I run from git shell : git pull origin master
	3) 
		a)  if no merge issues, great, I add commit and push code (to my branch). DONE
		b) if merge issues, I open files and fix merge issues, then follow steps in a)
	
	Scenario example 2:
	I want to merge my code into master
	
	1) Make sure I work with latest code FIRST
		a) perform steps as in example 1
	3) add, commit and push everything to own branch
	4) got o github, create pull requests.
		a) check for any merge issues with MASTER
		b) if teammates approve, confirm merge