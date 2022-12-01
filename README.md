# advent-of-code-2022
Advent of Code 2022, solved in Kotlin

# Usage Instructions

This year, I wanted to keep the boilerplate run code away from the logic we should be modifying, so I created individual day classes without a main method. Instead, there is a class called `Main.kt`. In there, you change out which class you are running the main method on.

### Run the code using DayXX

On line 5, change the `day` variable to the class that you're working with.

On line 4, change the `dayNum` variable to the day number you are working with. This changes which input files you are using.

### Run part 1 only

On line 6, change the `part1Only` variable to true. This skips the part2 of the class.

### Don't run tests for the day

on line 7, change the `includeTesting` variable to false. This skips the verification tests for the day.
