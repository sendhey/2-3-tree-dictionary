import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //create a twothreetree
        
		
		TwoThreeTree theTree = new TwoThreeTree();


        //go through the STDIN and add the queries to the twothreetree
        
        try{
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        	int dbSize = Integer.parseInt(br.readLine());
        	for (int i = 0; i < dbSize; i++){
        		
        		//System.out.println("HI");
        		
        		String temp = br.readLine();
        		String tempParts[] = temp.split(" ");
        		int tempType = Integer.parseInt(tempParts[0]);
        		String tempPlanet = tempParts[1];
        		if (tempType == 1){
        		int tempFee = Integer.parseInt(tempParts[2]);
        		
        		
//        		System.out.println("PLANET: " + tempPlanet + " FEE: " + tempFee);
        		
        		twothree.insert(tempPlanet, tempFee, theTree);
        		}
        		if (tempType == 2) {
        			Search(tempPlanet, theTree.root, theTree.height);

        		}
        	}
        }
        catch(IOException e){
        	e.printStackTrace();
        }
	}
	
	public static void Search(String query, Node root, int height) {
		if (height > 0){
			InternalNode inRoot = (InternalNode) root;
			if (inRoot.child0.guide.compareTo(query) >= 0){
//    			System.out.println(inRoot.child0.guide + " is greater than " + min);
    			Node child0 = (Node) inRoot.child0;
    			Search(query, child0, height-1);
    		}
    		else if ((inRoot.child1.guide.compareTo(query) >= 0) || (inRoot.child2 == null)){
//    			System.out.println(inRoot.child1.guide + " is greater than " + min);
    			Node child1 = (Node) inRoot.child1;
    			Search(query, child1, height-1);
    		}
    		else {
//    			System.out.println("WENT TO " + inRoot.child2.guide);
    			Node child2 = (Node) inRoot.child2;
    			Search(query, child2, height-1);
    		}
			
			
		}
		else{
			LeafNode leafRoot = (LeafNode) root;
			if (query.equals(leafRoot.guide)){
    			System.out.println(leafRoot.value);
    		}
    		else{
    			System.out.println("-1");    			
    		}
		}
	}
	

	 
	    public static int traverse(Node root, String query){
	    	
	    	if (root instanceof LeafNode){
	    		if (root.guide.compareTo(query) == 0){
	    			System.out.println(((LeafNode) root).value);
	    			
	    		}
	    	}
	    	else if (root instanceof InternalNode){
	    		traverse(((InternalNode) root).child0, query);
	    		traverse (((InternalNode) root).child1, query);
	    		traverse (((InternalNode) root).child2, query);
	    	}
	    	return 0;
    
	    }
}

class twothree {

   static void insert(String key, int value, TwoThreeTree tree) {
   // insert a key value pair into tree (overwrite existing value
   // if key is already present)

      int h = tree.height;

      if (h == -1) {
          LeafNode newLeaf = new LeafNode();
          newLeaf.guide = key;
          newLeaf.value = value;
          tree.root = newLeaf; 
          tree.height = 0;
      }
      else {
         WorkSpace ws = doInsert(key, value, tree.root, h);

         if (ws != null && ws.newNode != null) {
         // create a new root

            InternalNode newRoot = new InternalNode();
            if (ws.offset == 0) {
               newRoot.child0 = ws.newNode; 
               newRoot.child1 = tree.root;
            }
            else {
               newRoot.child0 = tree.root; 
               newRoot.child1 = ws.newNode;
            }
            resetGuide(newRoot);
            tree.root = newRoot;
            tree.height = h+1;
         }
      }
   }

   static WorkSpace doInsert(String key, int value, Node p, int h) {
   // auxiliary recursive routine for insert

      if (h == 0) {
         // we're at the leaf level, so compare and 
         // either update value or insert new leaf

         LeafNode leaf = (LeafNode) p; //downcast
         int cmp = key.compareTo(leaf.guide);

         if (cmp == 0) {
            leaf.value = value; 
            return null;
         }

         // create new leaf node and insert into tree
         LeafNode newLeaf = new LeafNode();
         newLeaf.guide = key; 
         newLeaf.value = value;

         int offset = (cmp < 0) ? 0 : 1;
         // offset == 0 => newLeaf inserted as left sibling
         // offset == 1 => newLeaf inserted as right sibling

         WorkSpace ws = new WorkSpace();
         ws.newNode = newLeaf;
         ws.offset = offset;
         ws.scratch = new Node[4];

         return ws;
      }
      else {
         InternalNode q = (InternalNode) p; // downcast
         int pos;
         WorkSpace ws;

         if (key.compareTo(q.child0.guide) <= 0) {
            pos = 0; 
            ws = doInsert(key, value, q.child0, h-1);
         }
         else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
            pos = 1;
            ws = doInsert(key, value, q.child1, h-1);
         }
         else {
            pos = 2; 
            ws = doInsert(key, value, q.child2, h-1);
         }

         if (ws != null) {
            if (ws.newNode != null) {
               // make ws.newNode child # pos + ws.offset of q

               int sz = copyOutChildren(q, ws.scratch);
               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
               if (sz == 2) {
                  ws.newNode = null;
                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
               }
               else {
                  ws.newNode = new InternalNode();
                  ws.offset = 1;
                  resetChildren(q, ws.scratch, 0, 2);
                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
               }
            }
            else if (ws.guideChanged) {
               ws.guideChanged = resetGuide(q);
            }
         }

         return ws;
      }
   }


   static int copyOutChildren(InternalNode q, Node[] x) {
   // copy children of q into x, and return # of children

      int sz = 2;
      x[0] = q.child0; x[1] = q.child1;
      if (q.child2 != null) {
         x[2] = q.child2; 
         sz = 3;
      }
      return sz;
   }

   static void insertNode(Node[] x, Node p, int sz, int pos) {
   // insert p in x[0..sz) at position pos,
   // moving existing extries to the right

      for (int i = sz; i > pos; i--)
         x[i] = x[i-1];

      x[pos] = p;
   }

   static boolean resetGuide(InternalNode q) {
   // reset q.guide, and return true if it changes.

      String oldGuide = q.guide;
      if (q.child2 != null)
         q.guide = q.child2.guide;
      else
         q.guide = q.child1.guide;

      return q.guide != oldGuide;
   }


   static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
   // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
   // also resets guide, and returns the result of that

      q.child0 = x[pos]; 
      q.child1 = x[pos+1];

      if (sz == 3) 
         q.child2 = x[pos+2];
      else
         q.child2 = null;

      return resetGuide(q);
   }
   

   
}


class Node {
   String guide;
   // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
   Node child0, child1, child2;
   // child0 and child1 are always non-null
   // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
   // guide points to the key

   int value;
}

class TwoThreeTree {
   Node root;
   int height;

   TwoThreeTree() {
      root = null;
      height = -1;
   }  
   
}

class WorkSpace {
// this class is used to hold return values for the recursive doInsert
// routine

   Node newNode;
   int offset;
   boolean guideChanged;
   Node[] scratch;
}

