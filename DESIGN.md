# Experiment design recorded before measurement

Revisions: after an initial exploratory run, the disjoint edit-distance input was added to expose the worst-case recursion. Before the final rerun, the edit sizes were corrected from 4, 6, 8, 10 to 2, 4, 8, 10 to satisfy the required fourfold range. The predictions and other choices remained unchanged.

Questions: How does edit-distance work grow for recursion and dynamic programming? How does pattern structure affect naive search and KMP?

Sizes: Edit distance uses equal lengths 2, 4, 8, 10; recursion is deliberately bounded. Search uses text lengths 2048, 4096, 8192, 16384 with a 32-character pattern. The largest sizes are five and eight times the smallest, respectively. Doubling search sizes separates growth from fixed overhead, and the pattern length stays fixed while being long enough to expose repeated comparisons.

Inputs: Fixed-seed random strings over ACGT, identical strings, and disjoint strings A...A versus B...B for edit distance distinguish typical data, the matching shortcut, and worst-case branching. Search uses fixed-seed random ACGT text and pattern, plus a worst-case text of A characters and a pattern of 31 A characters followed by B. The latter forces the naive method to recheck nearly the whole pattern at each position.

Metrics: Count recursive calls or DP cells computed for edit distance, and character comparisons in both search phases for search. Calls expose repeated subproblems, cells expose table work, and comparisons expose repeated checking. Calls and cells are different units; their ratio is not a measured speedup. Record elapsed milliseconds for each of five runs after three warm-ups; report the median. Counts are incremented within the algorithms. Time includes allocation and KMP preprocessing, excludes input generation and CSV writing, and captures implementation overhead.

Predictions: Recursion on mismatches grows much faster than the quadratic tables; identical inputs let recursion follow one branch. Table and two-row counters equal n squared. Naive search on the adversarial input approaches (n - 31) times 32 comparisons; KMP remains linear. Random inputs usually stop naive comparisons early.

Plots: (1) Edit work against n on a logarithmic y axis shows growth and the matching shortcut. (2) Search comparisons against n compares the two input types. (3) Median search time against n compares observed elapsed cost. All plots include units and a takeaway in the report.
