# TeraParser

TeraParser is preliminary implementation for Transition-Based parsing.

TeraParser supports following methods:
  - Online Learning and Structured Learning
  - Early-update and Max-violation

This currently does not supports:
  - Non-local feature learing
  - Raw text parsing
  - Labeled arc prediction

> Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.

### Version
0.8.0-b1 (beta)

### Tech

TeraParser uses following techniques:

  - 

## Installation

TeraParser works on java 1.8.0+

```sh
$ git clone [git-repo-url] TeraParser
$ cd TeraParser
$ ant
```

## Usage

```sh
Usage:
  java -jar build/TeraParser.jar COMMAND [OPTIONS]

Example:
  java -jar build/TeraParser.jar help
  java -jar build/TeraParser.jar train --trainfile <file> --devfile <file> [OPTIONS]
  java -jar build/TeraParser.jar parse --input <file> --modelin <file> [OPTIONS]

train options:
      --trainfile <file>     [required] Conll file to train
      --devfile <file>       [required] Conll file used as development set
      --testfile <file>      Conll file used for final testing (optional)
      --iteration            Training iteration (default: 20)
      --locally              Train greedily, otherwise train globally (structured learing)
      --beamwidth <num>      Train globally using beam-search with specified beam-width (default: 16)
      --early                Update weight with "early-update" method, otherwise use "max-violation"
      --modelout <file>      Output learned parameters to the specified file

parse options:
      --input <file>         [required] Target conll file to parse
      --modelin <file>       [required] Model file for parsing, which contains learing parameters
      --locally              Parse greedily, otherwise parse globally (structured parsing)
      --beamwidth <num>      Parse globally using beam-search with specified beam-width (default: 16)

common options:
      --config <file>        Specify options using config file
      --saveconfig <file>    Save current options to the file
      --verbose false        Turn off displaying messages to stdin and stderr
      --logdir <dir>         Log output directory (default: logs)
      --loglevel (info|off)  Log level (default: info)
```

### Train

### Parse

### Performance

More details coming soon.

### Todos

 - Write Javadoc and Tests


#### References

  - 


License
----
Apache License Version 2.0

Copyright 2016 Teranishi Hiroki

