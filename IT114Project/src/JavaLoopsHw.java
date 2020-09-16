public class JavaLoopsHw {
	public static void main(String[] args) {

		// IT114005 Andy Chang asc52

		// 1. Create an array/collection of numbers (initialize it with any
		// number of numbers (more than 1) in numerical order, with or without
		// duplicates)
		int[] arr = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };

		System.out.println("Print all elements of the array");
		// 2. Create a loop that loops over each number and shows their value.
		for (int index : arr) { // calls the element we are iterating over index from the array arr
			System.out.println(index); // for each index print out that element to the console
		}

		// 3. Have the loop output only even numbers regardless of how long the
		// array/collection is.

		System.out.println("Print only even numbers in the array");

		for (int index : arr) {
			if (index % 2 == 0) { // use if statement and the modulo operator and if remainder is 0 then its even
				System.out.println(index);
			}
		}
	}
}