(ns ice.core
  (:require
   [clojure.string :as str]
   [clojure.walk :as w]))

(def ^:dynamic *disable-colors*
  "If set to true, all color functions will return the input string as is."
  false)

(def ^:private reset
  (str "\033[" 0 "m"))

(defn- do-color [color-code args]
  (if *disable-colors*
    (apply str args)
    (str (str/join (map #(str color-code %) args)) reset)))

(defn strip-color
  "Removes all ANSI color and formatting codes from a string.
   Useful for logging to files, processing colored output, or testing."
  [s]
  (str/replace s #"\033\[[0-9;]*m" ""))

(defn- bold "Wrap a string with code to make it bold then resets it."
  [& args] (do-color "[1m" args))
(defn- dark "Wrap a string with code to make it dark then resets it."
  [& args] (do-color "[2m" args))
(defn- underline "Wrap a string with code to make it underline then resets it."
  [& args] (do-color "[4m" args))
(defn- blink "Wrap a string with code to make it blink then resets it."
  [& args] (do-color "[5m" args))
(defn- reverse-color "Wrap a string with code to make it reverse-color then resets it."
  [& args] (do-color "[7m" args))
(defn- concealed "Wrap a string with code to make it concealed then resets it."
  [& args] (do-color "[8m" args))
(defn- gray "Wrap a string with code to make it gray then resets it."
  [& args] (do-color "[30m" args))
(defn- grey "Wrap a string with code to make it grey then resets it."
  [& args] (do-color "[30m" args))
(defn- red "Wrap a string with code to make it red then resets it."
  [& args] (do-color "[31m" args))
(defn- green "Wrap a string with code to make it green then resets it."
  [& args] (do-color "[32m" args))
(defn- yellow "Wrap a string with code to make it yellow then resets it."
  [& args] (do-color "[33m" args))
(defn- blue "Wrap a string with code to make it blue then resets it."
  [& args] (do-color "[34m" args))
(defn- magenta "Wrap a string with code to make it magenta then resets it."
  [& args] (do-color "[35m" args))
(defn- cyan "Wrap a string with code to make it cyan then resets it."
  [& args] (do-color "[36m" args))
(defn- white "Wrap a string with code to make it white then resets it."
  [& args] (do-color "[37m" args))
(defn- on-grey "Wrap a string with code to make it on-grey then resets it."
  [& args] (do-color "[40m" args))
(defn- on-gray "Wrap a string with code to make it on-gray then resets it."
  [& args] (do-color "[40m" args))
(defn- on-red "Wrap a string with code to make it on-red then resets it."
  [& args] (do-color "[41m" args))
(defn- on-green "Wrap a string with code to make it on-green then resets it."
  [& args] (do-color "[42m" args))
(defn- on-yellow "Wrap a string with code to make it on-yellow then resets it."
  [& args] (do-color "[43m" args))
(defn- on-blue "Wrap a string with code to make it on-blue then resets it."
  [& args] (do-color "[44m" args))
(defn- on-magenta "Wrap a string with code to make it on-magenta then resets it."
  [& args] (do-color "[45m" args))
(defn- on-cyan "Wrap a string with code to make it on-cyan then resets it."
  [& args] (do-color "[46m" args))
(defn- on-white "Wrap a string with code to make it on-white then resets it."
  [& args] (do-color "[47m" args))

(def ^:private colors
  {:bold bold :dark dark :underline underline :blink blink :reverse-color reverse-color :concealed concealed
   :gray gray :grey grey :red red :green green :yellow yellow :blue blue :magenta magenta :cyan cyan :white white
   :on-grey on-grey :on-gray on-gray :on-red on-red :on-green on-green :on-yellow on-yellow :on-blue on-blue
   :on-magenta on-magenta :on-cyan on-cyan :on-white on-white})

(defn- ice->fn [x]
  (and (vector? x)
       (contains? colors (first x))
       (colors (first x))))

(defn- ice* [arg]
  (w/prewalk
    (fn [x]
      (if-let [color-fn (ice->fn x)]
        (apply color-fn (ice* (into [] (rest x))))
        x))
    arg))

(defn p
  "Iced out println: Takes strings and vector of strings and colors, and prints them out with the specified colors.

  Example:
  (ice/p [:red 1 [:blue 2 [:green 3] 2] 1])
  ;; =prints=> 12321
  ;; with red 1's, blue 2's and green 3's."
  [& args]
  (doseq [arg args]
    (print (ice* arg)))
  (println))

(defn without-trailing-newline
  "Removes the trailing newline from the output of a function."
  [s]
  (if (str/ends-with? s "\n")
    (subs s 0 (dec (count s)))
    s))

(defn p-str [& args]
  (without-trailing-newline
    (with-out-str (apply p args))))
