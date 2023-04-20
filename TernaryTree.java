import java.io.*;
import java.util.Scanner; 

public class TernaryTree {
    public static void main(String[] args) throws IOException {
        //Reading file names from arguments and creating file objects
        String[] list = new String[3];
        String inputFileName = args[0];
        String outputFileName = args[1];
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        boolean overwrite = true;

        //true if there was already a file created, otherwise it creates the file
        if (!outputFile.createNewFile()){
            overwrite = false;
        }
        Writer outputWriter = new FileWriter(outputFileName, overwrite);

        //scanner object to read file
        Scanner scan = new Scanner(inputFile);

        //tree object containing the nodes. Updated by the input commands
        Tree tree = new Tree();

        //try-catch-finally method for scanning & writing
        try {
            while (scan.hasNextLine()){  //while loop scans a line on each run
                String line = scan.nextLine();
                
                //conditions method checks the scanned line and calls neccessary method
                boolean error = conditions(line, tree);

                //if there was an error, adds "Input error." to the string to be printed and terminates program
                if (error) {
                    if (tree.printString.length() > 0) { //if a newline is needed
                        tree.printString = tree.printString + "\n";
                    }
                    tree.printString = tree.printString + "Input error.";
                    break; //out of while loop
                }
            }

            //outputting everything as commanded by input commands to output file
            outputWriter.write(tree.printString);
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //Closing scanner and writing
            scan.close();
            outputWriter.close();
        }
    }

    //method conditions checks the input conditions and calls the appropriate tree method
    //returns false if no error present, true otherwise
    public static boolean conditions(String line, Tree tree){
        //if line "Print()", call print method in tree
        if (line.equals("Print()")){
            tree.print();
        } 
        //else if line "Add*(a,b)" or "Exchange(a,b)" where * is L M or R and a b are strings without a blank character
        else if (line.length() >= 7 && line.charAt(line.length()-1) == ')' && ( (line.substring(0, 5).equals("AddL(")) || (line.substring(0, 5).equals("AddM(")) 
                || (line.substring(0, 5).equals("AddR(")) || (line.length() > 9 && (line.substring(0, 9).equals("Exchange("))) ))
        {
            //String containing argument a,b
            String arguments = line.substring(line.indexOf('(') + 1, line.length()-1);
            String a;
            String b;
            boolean dollar = false; //boolean for string b beginning with $ character
            int commaIndex = arguments.indexOf(','); //index of first comma
            //if argument contains a comma and non-empty strings a & b with no blank character
            if (arguments.contains(",") && !arguments.contains(" ") && commaIndex != arguments.length()-1 && commaIndex != 0) {
                if (arguments.charAt(commaIndex + 1) == ',') { //if character after comma is also a comma
                    int i = 2;
                    //go through all consecuting commas
                    while(arguments.charAt(commaIndex + i) == ',') {
                        i++;
                    }
                    a = arguments.substring(0, commaIndex + i-1); //string a gets all but the last of the consecutive commas
                    b = arguments.substring(commaIndex + i, arguments.length());
                }
                else {
                    //setting strings a & b with their appropriate arguments
                    a = arguments.substring(0, commaIndex);
                    b = arguments.substring(commaIndex+1, arguments.length());
                }
                if (b.charAt(0) == '$') { //if string b begins with $ sign, cut it from string b and set dollar to true
                    b = b.substring(1, b.length());
                    dollar = true;
                }

                //if Add argument
                if (line.substring(0, 3).equals("Add")) {
                    int leftMiddleRight; //value for left, middle or right Add method
                    //if AddL
                    if (line.charAt(3) == 'L') {
                        leftMiddleRight = 1;
                    }
                    //else if AddM
                    else if (line.charAt(3) == 'M') {
                        leftMiddleRight = 2;
                    }
                    //else will be AddR
                    else {
                        leftMiddleRight = 3;
                    }
                    tree.add(a, b, leftMiddleRight, dollar); //calling add method from tree
                }
                //else will be an Exchange argument
                else {
                    tree.exchange(a, b, dollar); //calling exchange method from tree
                }
            }
            //else Input error. So returns true
            else {
                return true;
            }
        }
        //else if line "Del*(a)" where * is L M or R and a is a string without a blank character
        else if (line.length() >= 7 && line.charAt(line.length()-1) == ')' && !line.contains(" ") && ( (line.substring(0, 5).equals("DelL(")) 
                || (line.substring(0, 5).equals("DelM(")) || (line.substring(0, 5).equals("DelR(")) ))
        {
            //String for argument a
            String a = line.substring(line.indexOf('(') + 1, line.length()-1);
            int leftMiddleRight; //value for left, middle or right Del method
            //if DelL
            if (line.charAt(3) == 'L') {
                leftMiddleRight = 1;
            }
            //else if DelM
            else if (line.charAt(3) == 'M') {
                leftMiddleRight = 2;
            }
            //else will be DelR
            else {
                leftMiddleRight = 3;
            }
            tree.del(a, leftMiddleRight); //calling del method from tree
        }
        else {
            return true; //return for input error
        }
        return false; //return for no input error in line
    }
}

//Tree class, contains the root Node which operates with its child nodes
class Tree {
    public Node root; //root Node of tree
    public String printString = ""; //string to be printed at the ende
    public int identifiedNodeLevel = 0; //Node level of the node identified thus far per commands

    //class constructor, creates root node
    public Tree(){
        root = new Node("root");
    }

    //class method to add a Node to the tree
    public void add(String a, String b, int leftMiddleRight, boolean dollar){
        Node identifiedNode = findNode(a, root, null, 0, true); //finding the node from which we're going to work from
        
        //if there exists a node with such payload
        if (identifiedNode != null){
            //if command to add to left child
            if (leftMiddleRight == 1) {
                if (identifiedNode.left == null){ //if no payload on left position
                    identifiedNode.setLeft(new Node(b)); //sets new node with payload b on left child
                }
                else if (dollar) { //else if dollar, overwrites the current payload string
                    identifiedNode.left.setPayload(b);
                }
                else { //already has payload in left position
                    if (printString.length() > 0) { //if newline is needed
                        printString = printString + "\n";
                    }
                    //adds "Add operation not possible." to string to be printed
                    printString = printString + "Add operation not possible.";
                }
            }
            //else if command to add to middle child
            else if (leftMiddleRight == 2) {
                if (identifiedNode.middle == null){ //if no payload on middle position
                    identifiedNode.setMiddle(new Node(b)); //sets new node with payload b on middle child
                }
                else if (dollar) { //else if dollar, overwrites the current payload string
                    identifiedNode.middle.setPayload(b);
                }
                else { //already has payload in middle position
                    if (printString.length() > 0) { //if newline is needed
                        printString = printString + "\n";
                    }
                    //adds "Add operation not possible." to string to be printed
                    printString = printString + "Add operation not possible.";
                }
            }
            //else will be command to add to right child
            else {
                if (identifiedNode.right == null){ //if no payload on right position
                    identifiedNode.setRight(new Node(b)); //sets new node with payload b on right child
                }
                else if (dollar) { //else if dollar, overwrites the current payload string
                    identifiedNode.right.setPayload(b);
                }
                else { //already has payload in right position
                    if (printString.length() > 0) { //if newline is needed
                        printString = printString + "\n";
                    }
                    //adds "Add operation not possible." to string to be printed
                    printString = printString + "Add operation not possible.";
                }
            }
        }

        identifiedNodeLevel = 0; //has to go back to zero for future use of findNode
    }

    //class method to del a Node or brance from the tree
    public void del(String a, int leftMiddleRight){
        Node identifiedNode = findNode(a, root, null, 0, false); //finding the node from which we're going to work from
        
        //if there exists a node with such payload
        if (identifiedNode != null){
            //if command to delete left child
            if (leftMiddleRight == 1) {
                identifiedNode.setLeft(null); //removes pointer of left child
            }
            //else if command to delete middle child
            else if (leftMiddleRight == 2) {
                identifiedNode.setMiddle(null); //removes pointer of middle child
            }
            //else will be command to delete right child
            else {
                identifiedNode.setRight(null); //removes pointer of right child
            }
        }

        identifiedNodeLevel = 0; //has to go back to zero for future use of findNode
    }

    //class method to exchange the payloads with string a to string b
    public void exchange(String a, String b, boolean dollar){
        boolean moreNodes = true; 
        //while more Nodes with payload a keep getting identified
        while(moreNodes){
            Node identifiedNode = findNode(a, root, null, 0, false); //finding the node from which we're going to work from
            //if there exists a node with such payload
            if (identifiedNode != null) {
                if (!dollar) { //if no dollar on beginning of string b, changes identified node payload to b
                    identifiedNode.setPayload(b);
                }
                else { //else, appends b to payload in identified node
                    identifiedNode.setPayload(identifiedNode.payload + b);
                }
            }
            else { //stops exchanging node values
                moreNodes = false;
            }
            identifiedNodeLevel = 0; //has to go back to zero for future use of findNode
        }
    }

    //class method to identify the node needed for add, del, and exchange methods
    public Node findNode(String a, Node currentNode, Node identifiedNode, int currentLevel, boolean add){
        if (currentNode.payload.equals(a)){ //if payload of current node is equal to string a
            if (identifiedNodeLevel != 0){
                //if current node is at a higher level than previously identified node, set it as the identified node
                if (currentLevel > identifiedNodeLevel){
                    identifiedNode = currentNode;
                    identifiedNodeLevel = currentLevel;
                }
                //if current node is at same level as previously identified node and we're calling this from add method
                //then set it as the identified node as it'll be the rightmost node in level
                if (add && currentLevel == identifiedNodeLevel){
                    identifiedNode = currentNode;
                    identifiedNodeLevel = currentLevel;
                }
            }
            else { //automatically sets it as identified node if no node has been identified yet
                identifiedNode = currentNode;
                identifiedNodeLevel = currentLevel;
            }
        }
        //keep going through tree, calling this function recursively.
        //updates the identifiedNode to keep track of the most current one
        if (currentNode.left != null){
            //going into left child
            identifiedNode = findNode(a, currentNode.left, identifiedNode, currentLevel + 1, add);
        }
        if (currentNode.middle != null){
            //going into left child
            identifiedNode = findNode(a, currentNode.middle, identifiedNode, currentLevel + 1, add);
        }
        if (currentNode.right != null){
            //going into left child
            identifiedNode = findNode(a, currentNode.right, identifiedNode, currentLevel + 1, add);
        }
        //returns the identifiedNode as of now
        return identifiedNode;
    }

    //class method to print tree to output file
    public void print(){
        int printLevel = 0; //level of tree that we're printing
        boolean none = false; //true only if no nodes on the current printLevel
        //if newline is needed
        if (printString.length() > 0 && root != null) {
            printString = printString + "\n";
        }
        //while there might be more nodes in next level
        while (!none){
            none = true;
            int currentLevel = 0;
            none = printing(root, printLevel, currentLevel, none); //calling recursive function to go through node
            if (!none) { //if there might be more nodes in next level, removes the " ; " from the string to be printed and adds newline
                printString = printString.substring(0, printString.length()-3);
                printString = printString + "\n";
            } 
            else { //else it only removes the " ; " from the string to be printed
                printString = printString.substring(0, printString.length()-1);
            }
            printLevel++; //increasing the level to be printed
        }
    }

    //method called within print which is used for recursion when printing the tree.
    //will return true if it didn't have any nodes that reached the printLevel. All the last nodes checked are leafs.
    public boolean printing(Node currentNode, int printLevel, int currentLevel, boolean none){
        //nodes go down the print level, if in print level, add to string to be printed
        if (currentLevel == printLevel) {
            none = false; //set to false as we didn't confirm node doesn't have children
            printString = printString + currentNode.payload + " ; ";
        }
        else if (!currentNode.isLeaf) { //if node is not a leaf, call this recursive function for its children
            if (currentNode.left != null) { //if has a left child
                none = printing(currentNode.left, printLevel, currentLevel + 1, none);
            }
            if (currentNode.middle != null) { //if has a left child
                none = printing(currentNode.middle, printLevel, currentLevel + 1, none);
            }
            if (currentNode.right != null) { //if has a left child
                none = printing(currentNode.right, printLevel, currentLevel + 1, none);
            }
        }
        return none;
    }
}

//Nodes for the tree, each node can have up to 3 child nodes
class Node {
    //class fields
    public String payload; //String payload of the node
    public boolean isLeaf; //true if Node is leaf, false otherwise
    public Node left = null; //left child
    public Node middle = null; //middle child
    public Node right = null; //right child

    //class constructor, sets payload and identifies node as lead
    public Node(String payload){
        this.payload = payload;
        this.isLeaf = true;
    }

    //setter for payload
    public void setPayload(String payload){
        this.payload = payload;
    }

    //setters for child nodes. Node is no longer a leaf
    public void setLeft(Node left){
        this.isLeaf = false;
        this.left = left;
    }
    public void setMiddle(Node middle){
        this.isLeaf = false;
        this.middle = middle;
    }
    public void setRight(Node right){
        this.isLeaf = false;
        this.right = right;
    }
}