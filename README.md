# jumski.midi-dataset-toolkit

Clojure library that helps with creating data sets from midi files.

* only intereseted in midi note on events
* processes those events per track
* produces multiline string of 0s and 1s on stdout
* each line represents a single step of note on events
* each "step" is notes playing at the same timestamp
* notes that plays at given step are encoded as `1` and all other are `0` (this allows for encoding chords)
* each line has 128 characters, which is a binary number, [little endian encoded](https://en.wikipedia.org/wiki/Endianness#Little-endian), which means bit 0 is on the right and bit 127 is on the left
* each bit corresponds to [midi note number](http://www.music.mcgill.ca/~ich/classes/mumt306/StandardMIDIfileformat.html#BMA1_3), from 0 to 127
* if bit is `1`, the note plays at given step, otherwise it is silent

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
