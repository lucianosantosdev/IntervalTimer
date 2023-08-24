#!/usr/bin/python3

import sys
import json
import git
import re
from os.path import isdir, join
from os import listdir, environ
import fileinput

def get_git_root():
    git_repo = git.Repo(".", search_parent_directories=True)
    return git_repo.git.rev_parse("--show-toplevel")

def get_version_from_git():
    git_repo = git.Repo(".", search_parent_directories=True)

    try:
        git_ref = git_repo.head.ref
        if "release/" in git_ref.name:
            return f'{git_ref.name.split("/")[1]}-RC'
    except:
        pass

    last_tag = git_repo.git.describe("--tags")
    return re.search(r'[0-9]+.[0-9]+.[0-9]+(-RC[0-9]*)?', last_tag)[0]
        
def set_app_version(version_name, version_code, target):
    gradle_file = f'{get_git_root()}/{target}/build.gradle.kts'

    with fileinput.FileInput(gradle_file, inplace=True) as file:
        for line in file:
            if "defaultConfig {" in line:
                print(line, end ='')
                print(f'versionName = "{version_name}"', end = '\n')
                print(f'versionCode = {version_code}', end = '\n')
            else:
                print(line, end ='')
                
def get_version_code(version_name, target):
    version_match = re.findall(r'[0-9]+', version_name)
    
    major = int(version_match[0])
    minor = int(version_match[1])
    patch = int(version_match[2])
    
    version_number = r'{}{}{}'
    run_number = int(environ.get('GITHUB_RUN_NUMBER', 0))
    target_version = '10' if target == 'mobile' else '30'
    
    return int(f'{major}{minor:02d}{patch:02d}{run_number:02d}{target_version}')
       
def main() -> int:
    target = sys.argv[1]
    version_name = get_version_from_git()
    version_code = get_version_code(version_name, target)
    set_app_version(version_name, version_code, target)

    print(f'versionName: {version_name}')
    print(f'versionCode: {version_code}')

if __name__ == '__main__':
    sys.exit(main())
