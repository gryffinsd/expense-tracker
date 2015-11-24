(ns expense_tracker.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [expense_tracker.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'expense_tracker.core-test))
    0
    1))
