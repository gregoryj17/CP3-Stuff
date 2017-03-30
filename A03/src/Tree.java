//Jackson Gregory
//Assignment 03: Self-Balancing Tree

import java.util.*;

/**
 * A binary search tree.
 *
 * @param <E> type of the tree
 */
public class Tree<E>
{
    private Node<E> root;

    /**
     * Adds an element to the tree.
     *
     * @param e element to be added
     */
    public void add(E e)
    {
        if(root==null){
            root=new Node<E>(e);
            root.tree=this;
        }
        else root.add(e);
    }

    public void add(E... e){
        for(E i : e){
            add(i);
        }
    }

    public boolean isEmpty(){
        return root==null;
    }

    public void removeMin(){
        root.getMinNode().remove();
    }

    public void removeMax(){
        root.getMaxNode().remove();
    }

    public void setRoot(Node<E> newRoot){
        root=newRoot;
    }

    /**
     * Gets an in order list of the elements.
     *
     * @return a string of the in order list
     */
    public String toString()
    {
        if(root==null)return "[]";
        return "Root: "+root.value+"\n"+"["+root+"]";
    }

    /**
     * Prints the tree where it actually looks like a tree.
     */
    public void prettyPrint()
    {
        if(root==null){
            System.out.println();
            return;
        }
        ArrayList<ArrayList<Node<E>>> pTree = new ArrayList<ArrayList<Node<E>>>();
        for(int i=0;i<=height();i++){
            pTree.add(new ArrayList<Node<E>>());
            if(i==0){
                pTree.get(0).add(root);
            }
            else{
                for(int j=0;j<pTree.get(i-1).size();j++){
                    if(pTree.get(i-1).get(j)==null){
                        pTree.get(i).add(null);
                        pTree.get(i).add(null);
                    }
                    else{
                        pTree.get(i).add(pTree.get(i-1).get(j).getLeft());
                        pTree.get(i).add(pTree.get(i-1).get(j).getRight());
                    }
                }
            }
        }
        for(int i=0;i<pTree.size();i++){
            for(int j=0;j<pTree.get(height()).size()-pTree.get(i).size()-1;j++){
                System.out.print("  ");
            }
            for(int j=0;j<pTree.get(i).size();j++){
                if(j!=0)for(int k=0;k<Math.pow(2,pTree.size()-i);k++){
                    System.out.print(" ");
                }
                if(pTree.get(i).get(j)==null)System.out.print(" ");
                else System.out.print(pTree.get(i).get(j).getValue());
            }
            System.out.println();
        }
    }

    /**
     * The height of the tree.  The number of edges between the root
     * and the lowest leaf.
     *
     * @return the height
     */
    public int height()
    {
        if(root==null)return -1;
        return root.getHeight();
    }

    /**
     * The number of elements in the tree.
     *
     * @return the number of elements in the tree.
     */
    public int size()
    {
        if(root==null)return 0;
        return root.size();
    }

    /**
     * Checks to see if e is in the tree
     *
     * @param e the element to search for
     * @return true if e is in tree
     */
    public boolean contains(E e)
    {
        if(root==null)return false;
        return root.contains(e);
    }

    /**
     * A list with all of the elements from the tree in order.
     *
     * @return A list of the elements in order.
     */
    public List<E> toList()
    {
        if(root==null)return null;
        return root.toList();
    }

    /**
     * Gets the largest value from the tree.
     *
     * @return the largest value of the tree.
     */
    public E max()
    {
        if(root==null)return null;
        return root.getMax();
    }

    /**
     * Gets the smallest value from the tree.
     *
     * @return the smallest value of the tree.
     */
    public E min()
    {
        if(root==null)return null;
        return root.getMin();
    }

    /**
     * Removes an element if found.
     *
     * @param e element to be removed
     * @return true if element found and removed
     */
    public boolean remove(E e)
    {
        if(root==null){
            //System.out.println("Tree is empty.");
            return false;
        }
        if(!contains(e)){
            //System.out.println("Tree does not contain "+e+".");
            return false;
        }
        Node<E> point=root.find(e);
        point.remove();
        return true;
    }

    public void rootRotateLeft(){
        Node<E> rnode = root.right;
        Node<E> lnode = rnode.left;
        Node<E> cnode = root;
        root=rnode;
        rnode.parent=null;
        rnode.left=cnode;
        cnode.parent=rnode;
        if(lnode!=null)lnode.parent=cnode;
        cnode.right=lnode;
        cnode.recalculateHeight();
        rnode.recalculateHeight();
        //if(lnode!=null)lnode.recalculateHeight();
    }

    public void rootRotateRight(){
        Node<E> lnode = root.left;
        Node<E> rnode = lnode.right;
        Node<E> cnode = root;
        root=lnode;
        lnode.parent=null;
        lnode.right=cnode;
        cnode.parent=lnode;
        cnode.left=rnode;
        if(rnode!=null)rnode.parent=cnode;
        cnode.recalculateHeight();
        lnode.recalculateHeight();
        //if(rnode!=null)rnode.recalculateHeight();
    }

    public void rootRemove(){
        root=null;
    }



    /*private boolean rootRemove() {
        ArrayList<E> stuff=root.restList();
        root=null;
        for(int i=0;i<stuff.size();i++){
            add(stuff.get(i));
        }
        return true;
    }*/

    public static void main(String[] args)
    {
		/*Tree<Integer> tree=new Tree<Integer>();
		tree.add(10);
		tree.add(100);
		tree.add(1);
		tree.add(5);
		tree.add(23);
		System.out.println(tree);
		System.out.println(tree.remove(10));
		System.out.println(tree);
		System.out.println(tree.height());
		tree.prettyPrint();*/
		/*for(int k=1;k<65;k*=2){
			int times=10;
			long tTime=0;
			int tSize=0;
			for(int j=0;j<times;j++){
				long startTime=System.nanoTime();
				int n = 100*k;
				TreeSet<Integer> b = new TreeSet<Integer>();
				for(int i = 0; i < n; i++)
				{
				    b.add( i );
				}
				long time=System.nanoTime()-startTime;
				double dtime=time/Math.pow(10,9);
				tTime+=time;
				tSize+=b.size();
				//System.out.println(dtime);
				//System.out.println(b.height());
			}
		double aTime=tTime/times;
		aTime/=Math.pow(10,9);
		int aSize=tSize/times;
		//System.out.println(k*100);
		System.out.println("Size: "+aSize+"\nTime: "+aTime+" seconds");
		}*/
        Tree<Integer> tree=new Tree<Integer>();
        for(int i=0;i<10;i++){
            tree.add(i+1);
        }
        System.out.println(tree+"\n"+tree.root.height+"\n"+tree.root.getMax());
        //tree.root.debugHeightPrint();

        //tree.prettyPrint();



    }

    //Add whatever you need to node.
    protected class Node<T>{
        protected T value;
        protected Node<T> left;
        protected Node<T> right;
        protected Node<T> parent;
        protected int height;
        protected Tree<T> tree;

        protected Node(T value)
        {
            this.value=value;
            this.height=0;
        }

        protected void debugHeightPrint(){
            if(left!=null)left.debugHeightPrint();
            System.out.println("Value: "+value+" Height: "+height);
            if(right!=null)right.debugHeightPrint();
        }

        public ArrayList<T> restList() {
            ArrayList<T> stuff=new ArrayList<T>();
            if(left!=null)stuff.addAll(left.toList());
            if(right!=null)stuff.addAll(right.toList());
            return stuff;
        }

        public boolean isLeaf(){
            if(left==null&&right==null)return true;
            return false;
        }

        public int size() {
            if(left==null&&right==null)return 1;
            if(left==null)return 1+right.size();
            if(right==null)return 1+left.size();
            return 1+left.size()+right.size();
        }

        protected T getValue()
        {
            return value;
        }

        protected Node<T> getLeft()
        {
            return left;
        }

        protected Node<T> getRight()
        {
            return right;
        }

        protected Node<T> getParent(){
            return parent;
        }

        protected int getHeight(){
            return height;
        }

        protected T getMin(){
            if(left==null)return value;
            return left.getMin();
        }

        protected Node<T> getMinNode(){
            if(left==null)return this;
            return left.getMinNode();
        }

        protected T getMax(){
            if(right==null)return value;
            return right.getMax();
        }

        protected Node<T> getMaxNode(){
            if(right==null)return this;
            return right.getMaxNode();
        }

        protected boolean contains(T t){
            if(value.equals(t))return true;
            if(left==null&&right==null)return false;
            if(left==null)return right.contains(t);
            if(right==null)return left.contains(t);
            return (left.contains(t)||right.contains(t));
        }

        protected Node<T> find(T t){
            if(value==t)return this;
            if(left==null&&right==null)return null;
            if(left!=null&&left.value.equals(t))return left;
            if(right!=null&&right.value.equals(t))return right;
            if(left==null)return right.find(t);
            if(right==null)return left.find(t);
            Node<T> temp = left.find(t);
            if(temp!=null)return temp;
            temp=right.find(t);
            if(temp!=null)return temp;
            return null;
        }

        @SuppressWarnings("unchecked")
        protected void add(T t)
        {
            Comparable<T> cv=(Comparable<T>)value;

            if(cv.compareTo(t)>0)
            {
                if(left==null){
                    left=new Node<T>(t);
                    left.height=0;
                    this.height=Math.max(left.height+1,this.height);
                    left.parent=this;
                    left.tree=tree;
                }
                else{
                    left.add(t);
                    this.height=Math.max(left.height+1,this.height);
                    while(isUnbalanced()){
                        if(left==null||right.height>left.height)rotateLeft();
                        else rotateRight();
                    }
                }
            }
            else
            {
                if(right==null){
                    right=new Node<T>(t);
                    right.height=0;
                    this.height=Math.max(this.height,right.height+1);
                    right.parent=this;
                    right.tree=tree;
                }
                else{
                    right.add(t);
                    this.height=Math.max(this.height,right.height+1);
                    while(isUnbalanced()){
                        if(left==null||right.height>left.height)rotateLeft();
                        else rotateRight();
                    }
                }
            }

        }
        protected ArrayList<T> toList(){
            ArrayList<T> list=new ArrayList<T>();
            if(left!=null) list.addAll(left.toList());
            list.add(value);
            if(right!=null) list.addAll(right.toList());
            return list;
        }

        protected void recalculateHeight(){
            if(left!=null&&right!=null){
                height=Math.max(left.height,right.height)+1;
            }
            else if(left!=null){
                height=left.height+1;
            }
            else if(right!=null){
                height=right.height+1;
            }
            else{
                height=0;
            }
        }

        protected boolean rotateLeft(){
            Node<T> pnode = this.parent;
            Node<T> rnode = this.right;
            Node<T> lnode = rnode.left;
            Node<T> cnode = this;
            if(rnode==null)return false;
            if(pnode==null){
                tree.rootRotateLeft();
                return true;
            }
            else{
                if(pnode.left==cnode)pnode.left=rnode;
                else pnode.right=rnode;
                rnode.parent=pnode;
                rnode.left=cnode;
                cnode.parent=rnode;
                cnode.right=lnode;
                if(lnode!=null)lnode.parent=cnode;
                cnode.recalculateHeight();
                rnode.recalculateHeight();
                //if(lnode!=null)lnode.recalculateHeight();
                pnode.recalculateHeight();
                return true;
            }
        }

        protected boolean rotateRight(){
            Node<T> pnode = this.parent;
            Node<T> lnode = this.left;
            if(lnode==null)return false;
            Node<T> rnode = lnode.right;
            Node<T> cnode = this;
            if(pnode==null){
                tree.rootRotateRight();
                return true;
            }
            else{
                if(pnode.left==cnode)pnode.left=lnode;
                else pnode.right=lnode;
                lnode.parent=pnode;
                lnode.right=cnode;
                cnode.parent=lnode;
                cnode.left=rnode;
                if(rnode!=null)rnode.parent=cnode;
                cnode.recalculateHeight();
                lnode.recalculateHeight();
                //if(rnode!=null)rnode.recalculateHeight();
                pnode.recalculateHeight();
                return true;
            }
        }

        protected void remove(){
            //System.out.println(value+" "+(left!=null?left.value:"")+" "+(right!=null?right.value:""));
            if(left==null&&right==null){
                if(parent!=null){
                    if(this==parent.left)parent.left=null;
                    else parent.right=null;
                    Node<T> node=parent;
                    parent.recalculateHeight();
                    while(node!=null){
                        while(node.isUnbalanced()){
                            if(node.left==null||node.right.height>node.left.height)node.rotateLeft();
                            else node.rotateRight();
                        }
                        node=node.parent;
                    }
                }
                else tree.rootRemove();
            }
            else if(left!=null){
                Node<T> temp = left.getMaxNode();
                value = temp.value;
                Node<T> node = temp.parent;
                temp.remove();
                while(node!=null){
                    while(node.isUnbalanced()){
                        if(node.left==null||node.right.height>node.left.height)node.rotateLeft();
                        else node.rotateRight();
                    }
                    node=node.parent;
                }
            }
            else{
                Node<T> temp = right.getMinNode();
                value = temp.value;
                Node<T> node = temp.parent;
                temp.remove();
                while(node!=null){
                    while(node.isUnbalanced()){
                        if(node.left==null||node.right.height>node.left.height)node.rotateLeft();
                        else node.rotateRight();
                    }
                    node=node.parent;
                }
            }
        }

        protected boolean isUnbalanced(){
            if(left==null&&right==null)return false;
            if(left==null&&right.height>0)return true;
            if(right==null&&left.height>0)return true;
            if(left==null||right==null)return false;
            return(Math.abs(left.height-right.height)>1);
        }

        /*private boolean remove(){
            if(right==null){
                Node<T> target=left;
                if(target.left==null&&target.right==null){
                    value=target.value;
                    left=null;
                    return true;
                }
                if(target.left==null){
                    ArrayList<T> stuff=target.right.toList();
                    value=target.value;
                    left=null;
                    for(int i=0;i<stuff.size();i++){
                        add(stuff.get(i));
                    }
                    return true;
                }
                if(target.right==null){
                    ArrayList<T> stuff=target.left.toList();
                    value=target.value;
                    left=null;
                    for(int i=0;i<stuff.size();i++){
                        add(stuff.get(i));
                    }
                    return true;
                }
                ArrayList<T> stuff=target.left.toList();
                stuff.addAll(target.right.toList());
                value=target.value;
                left=null;
                for(int i=0;i<stuff.size();i++){
                    add(stuff.get(i));
                }
                return true;
            }
            if(left==null){
                Node<T> target=right;
                if(target.left==null&&target.right==null){
                    value=target.value;
                    right=null;
                    return true;
                }
                if(target.left==null){
                    ArrayList<T> stuff=target.right.toList();
                    value=target.value;
                    right=null;
                    for(int i=0;i<stuff.size();i++){
                        add(stuff.get(i));
                    }
                    return true;
                }
                if(target.right==null){
                    ArrayList<T> stuff=target.left.toList();
                    value=target.value;
                    right=null;
                    for(int i=0;i<stuff.size();i++){
                        add(stuff.get(i));
                    }
                    return true;
                }
                ArrayList<T> stuff=target.left.toList();
                stuff.addAll(target.right.toList());
                value=target.value;
                right=null;
                for(int i=0;i<stuff.size();i++){
                    add(stuff.get(i));
                }
                return true;
            }
            ArrayList<T>stuff=left.toList();
            stuff.addAll(right.restList());
            value=right.value;
            left=null;
            right=null;
            for(int i=0;i<stuff.size();i++){
                add(stuff.get(i));
            }
            return true;
        }*/

        /**
         * toString for the Node
         * @return the string
         */
        public String toString()
        {
            String s="";
            if(left!=null) s+="[ "+left+", ]";
            s+=value;
            if(right!=null) s+=", ("+right+") ";
            return s;
        }
    }


}
