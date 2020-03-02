# jumski.midi-dataset-toolkit

Clojure library that helps with creating data sets from midi files.

* only intereseted in midi note on events
* processes those events them per track
* produces multiline string of 0s and 1s
* each line represents a single step of note on events, each `1` representing note that plays at this step (this covers chords)
* each line has 128 characters, which is a binary number, [little endian encoded](https://en.wikipedia.org/wiki/Endianness#Little-endian), which means bit 0 is on the right and bit 127 is on the left

## Usage

Just download `midi2stepfile` from latest release and run it, passing path to one or multiple midi files, like this:

```bash
midi2stepfile resources/c_major_scale.mid
midi2stepfile some/path/to/*.mid
```

Each track from each file will be printed to stdout encoded as `0`s and `1`s.

## Building

```bash
lein bin
```

The binary will be put into `bin/midi2stepfile`

## TODO:

[ ] add time quantization for steps
[ ] add some way to consume streams (stdinput first?)

## License

Copyright © 2020 Wojciech Majewski

MIT License
