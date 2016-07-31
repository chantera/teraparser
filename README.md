# TeraParser : Fast Transition-Based Parser

TeraParser is preliminary implementation for Transition-Based parsing.

TeraParser supports following methods:

  - Arc-Eager dependency parsing
  - Online Learning and Structured Learning
  - Early-update and Max-violation

This parser currently does not support:

  - Non-local feature learing
  - Raw text parsing
  - Labeled arc prediction

### Latest release

The most recent release is TeraParser 0.8.1-a2 (alpha), released July 31, 2016

## Installation

TeraParser requires JDK 1.8 or higher

```sh
$ git clone https://github.com/chantera/teraparser
$ cd teraparser
$ ant
```

Then you can try the parser using the following command:

```sh
$ java -jar build/teraparser.jar train --config sample/sample.properties
```

## Usage

```sh
Usage:
  java -jar build/teraparser.jar COMMAND [OPTIONS]

Example:
  java -jar build/teraparser.jar help
  java -jar build/teraparser.jar train --trainfile <file> --devfile <file> [OPTIONS]
  java -jar build/teraparser.jar parse --input <file> --modelin <file> [OPTIONS]

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

## Performance

More details coming soon.

## References

  - Collins, M., 2002, July. Discriminative training methods for hidden markov models: Theory and experiments with perceptron algorithms. In Proceedings of the ACL-02 conference on Empirical methods in natural language processing-Volume 10 (pp. 1-8). Association for Computational Linguistics.
  - Daume, H.C., 2006. Practical structured learning techniques for natural language processing. ProQuest.
  - Goldberg, Y. and Nivre, J., 2012. A Dynamic Oracle for Arc-Eager Dependency Parsing. In COLING (pp. 959-976).
  - Goldberg, Y. and Nivre, J., 2013. Training deterministic parsers with non-deterministic oracles. Transactions of the association for Computational Linguistics, 1, pp.403-414.
  - Huang, L. and Sagae, K., 2010, July. Dynamic programming for linear-time incremental parsing. In Proceedings of the 48th Annual Meeting of the Association for Computational Linguistics (pp. 1077-1086). Association for Computational Linguistics.
  - Huang, L., Fayong, S. and Guo, Y., 2012, June. Structured perceptron with inexact search. In Proceedings of the 2012 Conference of the North American Chapter of the Association for Computational Linguistics: Human Language Technologies (pp. 142-151). Association for Computational Linguistics.
  - Nivre, J., 2003. An efficient algorithm for projective dependency parsing. In Proceedings of the 8th International Workshop on Parsing Technologies (IWPT.
  - Nivre, J., 2008. Algorithms for deterministic incremental dependency parsing. Computational Linguistics, 34(4), pp.513-553.
  - Rasooli, M.S. and Tetreault, J., 2015. Yara parser: A fast and accurate dependency parser. arXiv preprint arXiv:1503.06733.
  - Yamada, H. and Matsumoto, Y., 2003, April. Statistical dependency analysis with support vector machines. In Proceedings of IWPT (Vol. 3, pp. 195-206).
  - Zhang, Y. and Clark, S., 2008, October. A tale of two parsers: investigating and combining graph-based and transition-based dependency parsing using beam-search. In Proceedings of the Conference on Empirical Methods in Natural Language Processing (pp. 562-571). Association for Computational Linguistics.
  - Zhang, Y. and Nivre, J., 2011, June. Transition-based dependency parsing with rich non-local features. In Proceedings of the 49th Annual Meeting of the Association for Computational Linguistics: Human Language Technologies: short papers-Volume 2 (pp. 188-193). Association for Computational Linguistics.
  - Zhang, Y. and Nivre, J., 2012, December. Analyzing the Effect of Global Learning and Beam-Search on Transition-Based Dependency Parsing. In COLING (Posters) (pp. 1391-1400).

License
----
Apache License Version 2.0

&copy; Copyright 2016 Teranishi Hiroki

