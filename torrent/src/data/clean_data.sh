#!/bin/bash

$(find . -maxdepth 1 -mindepth 1 -type f | egrep -v ".sh"| xargs rm  );
