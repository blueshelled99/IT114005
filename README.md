# IT114005
## Andy Chang

Git commands for adding:

git add [filename];
git commit -m "[some message]";
git push origin master; //push from machine to repo

Git commands for pulling:

git pull origin master; //pull from repo to machine

Git commands for removing files:

git rm [filename]; // do git rm --cached [filename]; to only remove from github and not the local file system
git commit -m "[some message]";
git push origin master;

GIT commands for branches:

git branch // shows all branches in our local repo
git branch -D branchName // deletes branch
git checkout branchName // attempts to checkout an existing branch rather than creating a new one
git checkout -b branchName // create a branch and switch to it automatically
git push origin MyFirstBranch // example of pushing to a branch like with master
git pull origin nameOfTargetBranch // pull changes from that branch to the existing working directory
git clone -b branchName ANewFolderName // targets a specific branch and clones it to that folder // make sure to it is not in an existing repo
git fetch origin // pull a list of all branches that exist on remote
