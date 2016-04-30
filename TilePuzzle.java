import java.util.*;

class TilePuzzle
{
	int[][] tileBoard;
	int[][] goalBoard;
	
	public static void main(String[] args)
	{
		Set<int[]> set = new HashSet<>();
		set.add(new int[] {0, 0, 1, 1});
		System.out.println(set.contains(new int[] {0,0,1,1}));
		
		TilePuzzle puzzle = new TilePuzzle(3);
		puzzle.printBoard(puzzle.tileBoard);
		puzzle.randomizeBoard(1000, puzzle.tileBoard);
		System.out.println();
		puzzle.printBoard(puzzle.tileBoard);
		System.out.println();
		
	}
	
	public TilePuzzle(int size)
	{
		tileBoard = new int[size][size];
		goalBoard = new int[size][size];
		for (int i = 0; i < tileBoard.length * tileBoard[0].length; i++)
		{
			tileBoard[i / tileBoard.length][i % tileBoard[0].length] = i;
			goalBoard[i / goalBoard.length][i % goalBoard[0].length] = i;
		}
	}
	
	public LinkedList<BoardNode> getSolveList()
	{
		BoardNode currentNode = solvePuzzle();
		LinkedList<BoardNode> list = new LinkedList<>();
		while (currentNode != null)
		{
			list.addFirst(currentNode);
			currentNode = currentNode.previous;
		}
		return list;
	}
	
	public BoardNode solvePuzzle()
	{
		PriorityQueue<BoardNode> queue = new PriorityQueue<>();
		// Enqueue starting board
		BoardNode current = new BoardNode(0, tileBoard, 0, manhattanDistance(tileBoard), null);
		queue.add(current);
		
		Set<List<Integer>> closedSet = new HashSet<>();
		
		while (!arraysAreEqual(current.tileBoard, goalBoard))
		{
			if (queue.isEmpty())
			{
				System.err.println("Queue is empty");
				break;
			}
			
			//printBoard(current.tileBoard);
			//System.out.println();
			// We have now found the shortest path to this node
			BoardNode bn = new BoardNode(1, goalBoard, 0, manhattanDistance(tileBoard), null);
			//System.out.println(closedSet.contains(bn));
			closedSet.add(getListFromArray(current.tileBoard));
			//try{
			//Thread.sleep(1000);} catch(InterruptedException e){;}
			
			int[] blankLocation = new int[] {-1, -1};
			outer: for (int r = 0; r < current.tileBoard.length; r++)
			{
				for (int c = 0; c < current.tileBoard[0].length; c++)
				{
					if (current.tileBoard[r][c] == 0)
					{
						blankLocation[0] = r;
						blankLocation[1] = c;
						break outer;
					}
				}
			}
			if (blankLocation[0] == -1)
			{
				System.err.println("Couldn't find blank tile");
				System.exit(1);
			}
			// Enqueue boards resulting from all possible moves, as long as they aren't in the closed set
			int[] x = new int[] {-1, 0, 0, 1};
			int[] y = new int[] {0, 1, -1, 0};
			for (int i = 0; i < x.length; i++)
			{
				int r = blankLocation[0] + y[i];
				int c = blankLocation[1] + x[i];
				if (r < 0 || r >= current.tileBoard.length || c < 0 || c >= current.tileBoard[0].length)
					continue;
					
				int[][] newBoard = new int[current.tileBoard.length][current.tileBoard[0].length];
				for (int j = 0; j < current.tileBoard.length; j++)
				{
					for (int k = 0; k < current.tileBoard[0].length; k++)
					{
						newBoard[j][k] = current.tileBoard[j][k];
					}
				}
				newBoard[blankLocation[0]][blankLocation[1]] = current.tileBoard[r][c];
				newBoard[r][c] = 0;
				
				if (closedSet.contains(getListFromArray(newBoard)))
				{
					continue;
				}
					
				queue.add(new BoardNode(current.number+1, newBoard, current.gCost+1, manhattanDistance(newBoard), current));
			}
			
			// Dequeue cheapest node
			current = queue.poll();
		}
		
		return current;
	}
	
	public ArrayList<Integer> getListFromArray(int[][] a)
	{
		ArrayList<Integer> list = new ArrayList<>();
		for (int r = 0; r < a.length; r++)
		{
			for (int c = 0; c < a[0].length; c++)
			{
				list.add(a[r][c]);
			}
		}
		return list;
	}
	
	public boolean arraysAreEqual(int[][] a, int[][] a2)
	{
		if (a == null || a2 == null) return false;
		
		for (int r = 0; r < a.length; r++)
		{
			for (int c = 0; c < a[0].length; c++)
			{
				if (a[r][c] != a2[r][c])
					return false;
			}
		}
		return true;
	}
	
	public int manhattanDistance(int[][] board)
	{
		int total = 0;
		for (int r = 0; r < board.length; r++)
		{
			for (int c = 0; c < board[0].length; c++)
			{
				int number = board[r][c];
				if (number == 0)
					continue;
				int goalRow = number / board.length;
				int goalCol = number % board[0].length;
				total += Math.abs(goalRow - r) + Math.abs(goalCol - c);
			}
		}
		return total;
	}
	
	public void randomizeBoard(int iterations, int[][] tileBoard)
	{
		int blankLoc[] = new int[] {-1,-1};
		for (int r = 0; r < tileBoard.length; r++)
		{
			for (int c = 0; c < tileBoard[0].length; c++)
			{
				if (tileBoard[r][c] == 0)
				{
					blankLoc[0] = r;
					blankLoc[1] = c;
				}
			}
		}
		if (blankLoc[0] == -1)
			System.err.println("Couldn't find blank tile");
		
		Random random = new Random();
		int[] x = {-1, 0, 0, 1};
		int[] y = {0, 1, -1, 0};
		for (int i = 0; i < iterations; i++)
		{
			int r2 = 0;
			int c2 = 0;
			do
			{
				int j = random.nextInt(x.length);
				r2 = blankLoc[0] + y[j];
				c2 = blankLoc[1] + x[j];
			}
			while (r2 < 0 || r2 >= tileBoard.length || c2 < 0 || c2 >= tileBoard[0].length);
			
			tileBoard[blankLoc[0]][blankLoc[1]] = tileBoard[r2][c2];
			tileBoard[r2][c2] = 0;
			blankLoc[0] = r2;
			blankLoc[1] = c2;
		}
	}
	
	public void printBoard(int[][] tileBoard)
	{
		for (int i = 0; i < tileBoard.length; i++)
		{
			for (int j = 0; j < tileBoard[0].length; j++)
			{
				System.out.print(tileBoard[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	class BoardNode implements Comparable<BoardNode>
	{
		int number;
		int[][] tileBoard;
		int gCost, hCost;
		BoardNode previous;
		public BoardNode(int number, int[][] tileBoard, int gCost, int hCost, BoardNode previous)
		{
			this.number = number;
			this.tileBoard = tileBoard;
			this.gCost = gCost;
			this.hCost = hCost;
			this.previous = previous;
		}
		public boolean equals(BoardNode otherNode) { return arraysAreEqual(tileBoard, otherNode.tileBoard); }
		@Override
		public int hashCode() { return tileBoard.hashCode(); }
		public int getCost() { return gCost + hCost; }
		public int compareTo(BoardNode otherNode) { return getCost() - otherNode.getCost(); }
	}
}