#! /usr/bin/env sh

cat README.md \
    | sed '
        \_\[!\[The Crisis in Storage Tech\](https://img.youtube.com/vi/GyUL3PbjWcw/maxresdefault.jpg)\](https://www.youtube.com/watch?v=GyUL3PbjWcw)_d;
        s_<!--\(<iframe.*iframe>\)-->_\1_;
        s_img/Twinned\_Effigy\_Recipe.png_https://cdn.modrinth.com/data/RWCrb0FL/images/9bd77766e59772770fada3fb5658727acac2cc4e.png_;
        s_- \[ \] _- _g;
        s_- \[x\] \(.*\)$_- ~~\1~~_g;'
