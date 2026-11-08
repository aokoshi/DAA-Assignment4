# DAA Assignment 4

Pashanov Yelnur, SE2523. Java 17 and Maven 3.8 or newer are required.

Repository: https://github.com/aokoshi/DAA-Assignment4

Submission version: branch `main`, tag [`v1.0`](https://github.com/aokoshi/DAA-Assignment4/tree/v1.0).

## Build and test

```sh
mvn test
```

The JUnit 5 suite covers the supplied reference values, empty cases, overlaps, counters and fixed-seed random agreement checks. It also compares both search algorithms with `String.indexOf` on 1,000 random cases. To compile without running tests, use `mvn package -DskipTests`.

## Reproduce the experiment

From the project root:

```sh
mvn test
java -cp target/classes daa.Experiment
```

This overwrites `results/results.csv`, with five measured runs per configuration after three warm-ups. Random input uses seed 20261108; input and work counters are reproducible, while elapsed times vary with the environment. To regenerate plots and the Word/Markdown reports, install Python 3.10+ with Pillow 10.1+ and python-docx (`python -m pip install Pillow python-docx`) and run `python make_report.py`. Numerical summaries are taken from the CSV; review the qualitative conclusions after changing the experiment. Export the DOCX to PDF in Word or LibreOffice to refresh `REPORT.pdf`.

`REPORT.pdf` is the four-page submission report; `DAA_Assignment4_Pashanov_Yelnur.docx` is its editable version and `REPORT.md` is readable on GitHub. The submitted measurements were collected on Windows using Microsoft OpenJDK 17.0.20.1. The CSV contains 260 measured runs; the JUnit suite contains eight tests.

`work` counts recursive calls, computed DP cells, or character comparisons including KMP preprocessing, depending on the family. See `DESIGN.md` and `REPORT.pdf` for the design and interpretation.
