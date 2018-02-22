# COMP-350
### Classwork for Comp 350 - Artificial Intelligence

#### Assignment 1 - 8-Puzzle (N-Puzzle)
- Randomly generates a valid N x N board to solve an N^2 - 1 puzzle (N = 3 is the standard 8 puzzle)
- The blank is represented by 0
- The goal state is all numbers in ascending order, followed by the blank
  - As an array: `int[] goalState = {1, 2, 3, 4, 5, 6, 7, 8, 0};` for the 8-puzzle
- Returns the solution path and writes it to a file upon completion
- __Warning:__ as expected, solving any board greater than 3x3 will result in exponential increases in runtime

There are 5 approaches the program can use to find a solution:
1. __Depth-First Search__ - Yes, DFS is a poor approach to the problem, but it's implemented nonetheless
2. __Breadth-First Search__
3. __Weighted Cost__ - Swap the blank (represented by the 0) with the greatest (in numeric value) adjacent tile 
4. __A* using number of misplaced tiles and moves made__
5. __A* using Manhattan Distance and moves made__
