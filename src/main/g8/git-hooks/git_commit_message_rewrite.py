import sys
import re
import os

try:
    from git import Repo
except ImportError:
    sys.exit('You need GitPython\n'
             'hint: pip3 install GitPython')

task_regex = re.compile('^$tasks-prefix$-[0-9]+')

def get_branch_task():
    try:
        repo = Repo(os.getcwd())
        found = task_regex.match(repo.active_branch.name.lower())
        if (found):
            return found.group(0)
        else:
            return None
    except:
        return None

def load_message(file):
    with open(file, 'r') as f:
        return f.read()

def write_message(file, msg):
    with open(file, 'w') as f:
        f.write(msg)

def message_starts_with_regex(msg, branch_task):
    lower_msg = msg.lower()
    lower_branch = branch_task.lower()
    return lower_msg.startswith(f'[{lower_branch}]')

def without_square_brackets(msg, branch_task):
    lower_msg = msg.lower()
    lower_branch = branch_task.lower()
    return lower_msg.startswith(lower_branch)

def rewrite_message(msg, branch_task):
    if not branch_task:
        return msg
    if message_starts_with_regex(msg, branch_task):
        return msg
    if without_square_brackets(msg, branch_task):
        return f'[{branch_task.upper()}] {msg[len(branch_task) + 1:]}'
    return f'[{branch_task.upper()}] {msg}'

def handle_message_rewrite(file):
    msg = load_message(file)
    active_task = get_branch_task()
    msg = rewrite_message(msg, active_task)
    write_message(file, msg)


if __name__ == '__main__':
    import git.exc
    if len(sys.argv) == 2:
        try:
            file = sys.argv[1]
            handle_message_rewrite(file)
        except git.exc.InvalidGitRepositoryError as error:
            sys.exit(f'Not a git repository: {error}')
