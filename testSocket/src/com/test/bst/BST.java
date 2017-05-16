package com.test.bst;

import java.util.NoSuchElementException;

/**
 * 二叉查找树
 *二叉查找树最重要的一个特征就是：每个结点都含有一个Comparable的键及其相关联的值，该结点的键要大于左子树中所有结点的键，而小于右子树中所有结点的键。
 *用内部类Node来替代以表示树中的结点，每个Node对象都含有一对键值(key和val)，两条链接(left和right)，和子节点计数器(size)。
 *另外我们还提前实现了size(), isEmpty()和contains()这几个基础方法，三种分别用来计算二叉树中的结点数目，判断二叉树是否为空，判断二叉树中是否含有包含指定键的结点。
 * @param <Key>
 * @param <Value>
 */
public class BST<Key extends Comparable<Key>, Value> {

	private Node root;             // root of BST
    private class Node {
        private Key key;           // sorted by key
        private Value val;         // associated data
        private Node left, right;  // left and right subtrees
        private int size;          // number of nodes in subtree
        public Node(Key key, Value val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }
    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return size(root);
    }
    // Returns number of key-value pairs in BST rooted at x.
    private int size(Node x) {
        if(x == null) {
            return 0;
        } else {
            return x.size;
        }
    }
    // Returns true if this symbol table is empty.
    public boolean isEmpty() {
        return size() == 0;
    }
    // Returns true if this symbol table contains key and false otherwise.
    public boolean contains(Key key) {
        if(key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        } else {
            return get(key) != null;
        }
    }
    
    /**
     * 查找操作
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and null if the key is not in the symbol table
     * @throws IllegalArgumentException if key is null
     * 
     * 如果查找的键key小于根结点的key，说明我们要查找的键如果存在的话肯定在左子树，因为左子树中的结点都要小于根结点，接下来我们继续递归遍历左子树。
     * 如果要查找的键key大于根结点的key，说明我们要查找的键如果存在的话肯定在右子树中，因为右子树中的结点都要大于根节点，接下来我们继续递归遍历右子树。
     * 如果要查找的键key等于根结点的key，那么我们就直接返回根结点的val。
     * 
     */
    public Value get(Key key) {
        if(key == null) {
            throw new IllegalArgumentException("first argument to put() is null");
        } else {
            return get(root, key);
        }
    }
    private Value get(Node x, Key key) {
        if(x == null) {
            return null;
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp < 0) {
                return get(x.left, key);
            } else if(cmp > 0) {
                return get(x.right, key);
            } else {
                return x.val;
            }
        }
    }
    
    
    /**
     * 插入操作
     * Inserts the specified key-value pair into the symbol table, overwriting the old
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is null.
     * 
     * 首先要找到我们新插入结点的位置，其思想和查找操作一样。
     * 找到插入的位置后我们就将新结点插入二叉树。只是这里还要加一个步骤：更新结点的size，因为我们刚刚新插入了结点，该结点的父节点，父节点的父节点的size都要加一。
     *
     * @param  key the key
     * @param  val the value
     * @throws IllegalArgumentException if key is null
     */
    public void put(Key key, Value val) {
        if(key == null) {
            throw new IllegalArgumentException("first argument to put() is null");
        }
        if(val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
        // assert check(); // Check integrity of BST data structure.
    }
    private Node put(Node x, Key key, Value val) {
        if(x == null) {
            return new Node(key, val, 1);
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp < 0) {
                x.left = put(x.left, key, val);
            } else if(cmp > 0) {
                x.right = put(x.right, key, val);
            } else {
                x.val = val;
            }
            // reset links and increment counts on the way up
            x.size = size(x.left) + size(x.right) + 1;
            return x;
        }
    }
    
    /**
     * 删除操作
     * Removes the smallest key and associated value from the symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        } else {
            root = deleteMin(root);
            // assert check(); // Check integrity of BST data structure.
        }
    }
    private Node deleteMin(Node x) {
        if(x.left == null) {
            return x.right;
        } else {
            x.left = deleteMin(x.left);
            x.size = size(x.left) + size(x.right) + 1;
            return x;
        }
    }
    
    /**
     * 删除最大的结点
     * Removes the largest key and associated value from the symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        } else {
            root = deleteMax(root);
            // assert check(); // Check integrity of BST data structure.
        }
    }
    private Node deleteMax(Node x) {
        if (x.right == null) {
            return x.left;
        } else {
            x.right = deleteMax(x.right);
            x.size = size(x.left) + size(x.right) + 1;
            return x;
        }
    }
    
    /**
     * Removes the specified key and its associated value from this symbol table
     * (if the key is in this symbol table).
     *
     * @param  key the key
     * @throws IllegalArgumentException if key is null
     */
    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to delete() is null");
        } else {
            root = delete(root, key);
            // assert check(); // Check integrity of BST data structure.
        }
    }
    private Node delete(Node x, Key key) {
        if(x == null) {
            return null;
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp < 0) {
                x.left = delete(x.left, key);
            } else if(cmp > 0) {
                x.right = delete(x.right, key);
            } else {
                // find key
                if(x.right == null) {
                    return x.left;
                } else if(x.left == null) {
                    return x.right;
                } else {
                    Node t = x;
                    x = deleteMin(t.right);
                    x.right = deleteMin(t.right);
                    x.left = t.left;
                }
            }
            // update links and node count after recursive calls
            x.size = size(x.left) + size(x.right) + 1;
            return x;
        }
    }
	
    /**
     * select的实现 - select的实现如下，实际就是根据左子树的结点数目来判断当前结点在二叉树中的大小。
     * Return the kth smallest key in the symbol table.
     *
     * @param  k the order statistic
     * @return the kth smallest key in the symbol table
     * @throws IllegalArgumentException unless k is between 0 and n-1
     */
    public Key select(int k) {
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("called select() with invalid argument: " + k);
        } else {
            Node x = select(root, k);
            return x.key;
        }
    }
    // Return the key of rank k.
    public Node select(Node x, int k) {
        if(x == null) {
            return null;
        } else {
            int t = size(x.left);
            if(t > k) {
                return select(x.left, k);
            } else if(t < k) {
                return select(x.right, k);
            } else {
                return x;
            }
        }
    }
    
    /**
     * rank的实现  -  rank就是查找指定的键key在二叉树中的排名
     * Return the number of keys in the symbol table strictly less than key.
     *
     * @param  key the key
     * @return the number of keys in the symbol table strictly less than key
     * @throws IllegalArgumentException if key is null
     */
    public int rank(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to rank() is null");
        } else {
            return rank(key, root);
        }
    }
    public int rank(Key key, Node x) {
        if(x == null) {
            return 0;
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp < 0) {
                return rank(key, x.left);
            } else if(cmp > 0) {
                return 1 + size(x.left) + rank(key, x.right);
            } else {
                return size(x.left);
            }
        }
    }
    
    
    /**
     * floor()要实现的就是向下取整
     * 如果指定的键key小于根节点的键，那么小于等于key的最大结点肯定就在左子树中了。
     * 如果指定的键key大于根结点的键，情况就要复杂一些，这个时候要分两种情况：
     * 1>当右子树中存在小于等于key的结点时，小于等于key的最大结点则在右子树中；
     * 2>反之根节点自身就是小于等于key的最大结点了。
     * Returns the largest key in the symbol table less than or equal to key.
     *
     * @param  key the key
     * @return the largest key in the symbol table less than or equal to key
     * @throws NoSuchElementException if there is no such key
     * @throws IllegalArgumentException if  key is null
     */
    public Key floor(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to floor() is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("called floor() with empty symbol table");
        }
        Node x = floor(root, key);
        if (x == null) {
            return null;
        } else {
            return x.key;
        }
    }
    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp == 0) {
                return x;
            } else if(cmp < 0) {
                return floor(x.left, key);
            } else {
                Node t = floor(x.right, key);
                if(t != null) {
                    return t;
                } else {
                    return x;
                }
            }
        }
    }
    
    /**
     * ceiling()则与floor()相反，它做的是向下取整，即找到大于等于key的最小结点。但是两者的实现思路是一致的，只要将上面的左变为右，小于变为大于就行了
     * Returns the smallest key in the symbol table greater than or equal to {@code key}.
     *
     * @param  key the key
     * @return the smallest key in the symbol table greater than or equal to {@code key}
     * @throws NoSuchElementException if there is no such key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Key ceiling(Key key) {
        if(key == null) {
            throw new IllegalArgumentException("argument to ceiling() is null");
        }
        if(isEmpty()) {
            throw new NoSuchElementException("called ceiling() with empty symbol table");
        }
        Node x = ceiling(root, key);
        if(x == null) {
            return null;
        } else {
            return x.key;
        }
    }
    private Node ceiling(Node x, Key key) {
        if(x == null) {
            return null;
        } else {
            int cmp = key.compareTo(x.key);
            if(cmp == 0) {
                return x;
            } else if(cmp < 0) {
                Node t = ceiling(x.left, key);
                if (t != null) {
                    return t;
                } else {
                    return x;
                }
            } else {
                return ceiling(x.right, key);
            }
        }
    }
    
}
