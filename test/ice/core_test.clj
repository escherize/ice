; test/ice/core_test.clj - Test namespace for ice.core
(ns ice.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [clojure.string :as str]
            [ice.core :as ice]))

;; Setup fixture for disabling/enabling colors in tests
(defn color-fixture [f]
  (f)  ; Run tests with colors enabled (default)

  ;; Re-run tests with colors disabled
  (binding [ice/*disable-colors* true]
    (f)))

(use-fixtures :each color-fixture)

(deftest p-str-basic-colors-test
  (testing "Basic usage of colors via p-str"
    (doseq [color-key (keys @#'ice.core/colors)]
      (testing (str "Testing " color-key " via p-str")
        (let [result (ice/p-str [color-key "test"])]
          ;; When colors are enabled (default)
          (when-not ice/*disable-colors*
            (is (str/includes? result "\033[") "Should contain ANSI escape code")
            (is (str/includes? result "m") "Should contain ANSI format specifier"))

          ;; When colors are disabled
          (when ice/*disable-colors*
            (is (= result "test") "Should return plain text when colors disabled")))))))

(deftest p-str-nested-colors-test
  (testing "Nested color usage via p-str"
    (let [nested-expr [:red "a" [:blue "b"] "c"]
          result (ice/p-str nested-expr)]

      (is (str/includes? (ice/strip-color result) "abc") "Should contain all characters")

      ;; With colors enabled
      (when-not ice/*disable-colors*
        (is (str/includes? result "\033[31m") "Should contain red color code")
        (is (str/includes? result "\033[34m") "Should contain blue color code")
        (is (str/includes? result "\033[0m") "Should contain reset code"))

      ;; With colors disabled
      (when ice/*disable-colors*
        (is (= result "abc") "Should return plain text when colors disabled")))))

(deftest p-str-complex-nesting-test
  (testing "Complex nested colors with p-str"
    (let [complex-expr [:red "1" [:blue "2" [:green "3"] "2"] "1"]
          result (ice/p-str complex-expr)]

      (is (str/includes? (ice/strip-color result) "12321") "Should contain all digits in order")

      ;; With colors enabled
      (when-not ice/*disable-colors*
        (let [reset-count (count (re-seq #"\033\[0m" result))]
          (is (> reset-count 0) "Should contain reset codes")
          (is (str/includes? result "\033[31m") "Should contain red color code")
          (is (str/includes? result "\033[34m") "Should contain blue color code")
          (is (str/includes? result "\033[32m") "Should contain green color code")))

      ;; With colors disabled
      (when ice/*disable-colors*
        (is (= result "12321") "Should return plain text when colors disabled")))))

(deftest p-str-multiple-arguments-test
  (testing "Multiple arguments to p-str"
    (let [result (ice/p-str [:red "Error: "] [:bold "Failed!"] " Please try again.")]

      (is (and (str/includes? result "Error: ")
               (str/includes? result "Failed!")
               (str/includes? result "Please try again."))
          "Should contain all text from multiple arguments")

      ;; With colors enabled
      (when-not ice/*disable-colors*
        (is (str/includes? result "\033[31m") "Should contain red color code")
        (is (str/includes? result "\033[1m") "Should contain bold code")
        (is (> (count (re-seq #"\033\[0m" result)) 1)
            "Should contain multiple reset codes"))

      ;; With colors disabled
      (when ice/*disable-colors*
        (is (= result "Error: Failed! Please try again.")
            "Should return plain text when colors disabled")))))

(deftest p-function-test
  (testing "p function outputs to *out*"
    (let [test-expr [:green "Success!"]
          expected-output (str (ice/p-str test-expr) "\n")
          captured-output (with-out-str (ice/p test-expr))]

      (is (= captured-output expected-output)
          "p should print the p-str result plus a newline")
      (is (str/ends-with? captured-output "\n")
          "p output should end with newline"))))

(deftest disable-colors-test
  (testing "Dynamic var *disable-colors* effects"
    (let [test-expr [:red "Error!"]]

      (is (binding [ice/*disable-colors* false]
            (not= "Error!" (ice/p-str test-expr)))
          "Default output should have color codes")
      (is (= "Error!"
             (binding [ice/*disable-colors* true]
               (ice/p-str test-expr)))
          "Output with *disable-colors* should be plain text"))))

(deftest all-colors-vector-test
  (testing "Testing all color keys in a vector"
    (let [all-colors-expr (vec
                           (mapcat (fn [color-key]
                                     [color-key (str (name color-key) " ")])
                                   (keys @#'ice.core/colors)))
          result (ice/p-str all-colors-expr)]

      ;; With colors enabled
      (when-not ice/*disable-colors*
        (is (str/includes? result "\033[") "Should contain ANSI codes"))

      ;; With colors disabled
      (when ice/*disable-colors*
        (is (not (str/includes? result "\033["))
            "Should not contain ANSI codes when disabled")))))

(deftest p-str-empty-test
  (testing "Empty arguments to p-str"
    (is (= (ice/p-str) "") "Empty p-str should return empty string")
    (is (= (with-out-str (ice/p)) "\n") "Empty p should print just a newline")))

(deftest p-str-non-vector-test
  (testing "Non-vector arguments to p-str"
    (is (= (ice/p-str "plain string") "plain string")
        "Plain string should be returned as-is")
    (is (= (ice/p-str 123) "123")
        "Numbers should be converted to strings")
    (is (= (ice/p-str nil) "nil")
        "nil should print as nil")))
