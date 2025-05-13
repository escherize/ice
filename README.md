# ice

A lightweight terminal color formatting library for Clojure.

[![Clojars Project](https://img.shields.io/clojars/v/io.github.escherize/ice.svg)](https://clojars.org/io.github.escherize/ice)

## Installation

### deps.edn

```clojure
{:deps 
 {io.github.escherize/ice {:git/tag "v0.1.0" :git/sha "..."}}}
```

## Usage

```clojure
(require '[ice.core :as ice])

;; Basic colored output
;; p is for printing
(ice/p [:red "This text is red"])
(ice/p [:on-blue "This text has blue background"])
(ice/p [:bold "This text is bold"])

;; Combining colors with nesting
(ice/p [:red "This is red " [:blue "and this is blue"]])

;; Complex nested formatting
(ice/p [:red "Error: " [:bold "Operation failed"] " - please try again"])

;; Creating a color demo
(ice/p (vec 
        (mapcat (fn [color]
                  [color (name color) " "]) 
                [:red :green :blue :yellow :cyan :magenta])))

;; Getting colored string without printing (no newline added)
(def colored-text (ice/p-str [:green "Success!"]))
;; => Returns colored string without printing and without a trailing newline

;; Disable colors (useful for logging to files)
(binding [ice/*disable-colors* true]
  (ice/p [:red "This will not be colored"]))

;; Multiple arguments
(ice/p [:red "Error: "] [:bold "Failed!"] " Please try again.")
```

## Functions

### `p`

The `p` function prints colored text to `*out*`, always adding a newline at the end:

```clojure
;; Print red text
(ice/p [:red "This is red"])

;; Print nested colors
(ice/p [:red "Red" [:blue " and blue"]])

;; Print multiple arguments
(ice/p [:red "Error:"] " " [:bold "Something went wrong"])
```

### `p-str`

The `p-str` function returns a colored string without printing it and without the trailing newline:

```clojure
;; Get red text as a string
(def red-text (ice/p-str [:red "This is red"]))
;; => "\033[31mThis is red\033[0m"

;; Use in string operations
(str "Prefix: " (ice/p-str [:green "Success"]) " :Suffix")
;; => "Prefix: \033[32mSuccess\033[0m :Suffix"

;; Useful for logging
(log/info (ice/p-str [:yellow "Warning:"] " " [:bold "Disk space low"]))
```

Note: Unlike many other Clojure string functions that add newlines, `p-str` does NOT add a trailing newline, so you can use it with println.



``` clojure
(def red-text (ice/p-str [:red "This is red"]))

(= "This is red" (ice/strip-color red-text))
;; => true
```

All the color options that can be used as the first element in vectors:

### Text Colors
- `:red`
- `:green` 
- `:yellow`
- `:blue`
- `:magenta`
- `:cyan`
- `:white`
- `:gray`/`:grey`

### Background Colors
- `:on-red`
- `:on-green`
- `:on-yellow`
- `:on-blue`
- `:on-magenta`
- `:on-cyan`
- `:on-white`
- `:on-gray`/`:on-grey`

### Text Formatting
- `:bold`
- `:dark`
- `:underline`
- `:blink`
- `:reverse-color`
- `:concealed`

## License

Copyright © 2025 io.github.escherize

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at:
https://www.eclipse.org/legal/epl-2.0/
