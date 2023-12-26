#!/bin/bash

cd "\$(dirname "\$0")"

commit_msg_path="../.git/hooks/commit-msg"
cp commit-msg.sh \$commit_msg_path
chmod u+x \$commit_msg_path
