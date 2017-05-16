package com.test.redblack;

/**
 *定义一棵红黑树
 *1-每个节点要么是红色，要么是黑色。红链接均为左链接。
 *2-红色节点不能连续（也即是，红色节点的孩子和父亲都不能是红色）。没有任何一个节点同时和两条红链接相连。根节点必须是黑色
 *3-任意空链接到根节点的路径上的黑链接数目相同。对于每个节点，从该点至null（树尾端）的任何路径，都含有相同个数的黑色节点。p.s: 我们将指向一棵空树的链接称为空链接。
 *
 *将链接的颜色保存在表示该结点的Node对象中的color变量中
 *如果指向它的链接是红色的，那么该变量为true，黑色则为false，我们规定空链接都为黑色。
 *
 *构造时传入的比较器（Comparator）
 */
public class RedBlackTree<Key extends Comparable<Key>, Value> {
	
	private static final boolean RED   = true;
    private static final boolean BLACK = false;
    
    // 树的根节点
    private Node root;     
    //键值对数
    private int n;  
    
    //辅助节点的数据类型
    private class Node {
    	
        private Key key;           // key
        private Value val;         // key对应相关的数据
        private Node left, right;  // 左和右子树的链接
        private boolean color;     // 父链颜色
        
        public Node(Key key, Value val, boolean color) {
            this.key = key;
            this.val = val;
            this.color = color;
        }
    }
    
    // 节点x是红色（和非null）?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }
    
    /**
     * 左旋转
     * 有一条红色的右链接，现在我们想要将这条红色右链接转换为左链接，这个操作过程就叫做左旋转
     * @param h
     * @return
     */
    private Node rotateLeft(Node h) {
        assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }
    
    /**
     * 右旋转
     * 右旋转的实现和左旋转的实现是类似的，就是将一个红色左链接转换成一个红色右链接
     * @param h
     * @return
     */
    private Node rotateRight(Node h) {
        assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }
    
    /**
     * 颜色转换
     * 如果一个结点的两个子结点均为红色链接，我们就将子结点的颜色全部由红色转换成黑色，同时将父结点的颜色由黑变红。
     * @param h
     */
    private void flipColors(Node h) {
        assert !isRed(h) && isRed(h.left) && isRed(h.right);
        h.color = RED;
        h.left.color = BLACK;
        h.right.color = BLACK;
    }
    
    /**
     * 插入操作的实现
     * 1-插入的新结点c大于树中现存的两个键，所以我们要将它连接到b结点的右链接。因为这个时候b的两条链接都是红链接，所以我们要进行flipColors。
     * 2-插入的新结点a要比树中现存的两个键都要小，所以我们先将它连接到结点b的左链接，前面我们提到红黑树的一个特性就是没有任何一个节点可以同时和两个红色链接相连，而现在b结点却违背了这一原则，所以我们要进行右旋转操作，接下来情形1是一模一样的了。
     * 3-插入的新结点b在树中现存的两个键之间，所以我们先将它连接到结点a的右链接，前面我们提到红黑树中红色链接都是左链接，所以我们首先要进行左旋转操作，接下来就和情形2一模一样了。
     * @param key
     * @param val
     */
    public void put(Key key, Value val) {
        root = insert(root, key, val);
        root.color = BLACK;
        // 红黑树数据结构完整性检查.
        // assert check(); 
    }
    private Node insert(Node h, Key key, Value val) {
        if (h == null) {
            n++;
            return new Node(key, val, RED);
        }
        int cmp = key.compareTo(h.key);
        if(cmp < 0) {
        	h.left  = insert(h.left,  key, val);
        }else if (cmp > 0) {
        	h.right = insert(h.right, key, val);
        }else{
        	h.val   = val;
        }
        // 修正任何右倾链接
        if (isRed(h.right) && !isRed(h.left)){
        	h = rotateLeft(h);
        }
        if (isRed(h.left)  &&  isRed(h.left.left)){
        	h = rotateRight(h);
        }
        if (isRed(h.left)  &&  isRed(h.right)) { 
        	flipColors(h);
        }
        return h;
    }
    
    
    public static void main(String[] args) {
    	//TODO:
    	
	}
}
