package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Token;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public enum Action {
    LEFT(0) {
        @Override
        protected State apply(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
            Token head = buffer.get(0);
            Token dependent = stack.remove(stack.size() - 1);
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }

        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            Token head = buffer.getFirst();
            Token dependent = stack.removeLast();
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }
    },
    RIGHT(1) {
        @Override
        protected State apply(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
            Token head = stack.get(stack.size() - 1);
            Token dependent = buffer.remove(0);
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }

        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            Token head = stack.getLast();
            Token dependent = buffer.removeFirst();
            stack.add(dependent);
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }
    },
    SHIFT(2) {
        @Override
        protected State apply(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
            stack.add(buffer.remove(0));
            return null;
        }

        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.add(buffer.removeFirst());
            return null;
        }
    },
    REDUCE(3) {
        @Override
        protected State apply(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
            stack.remove(stack.size() - 1);
            return null;
        }

        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.removeLast();
            return null;
        }
    };

    public final int index;
    public static int SIZE = values().length;

    Action(int index) {
        this.index = index;
    }

    protected abstract State apply(List<Token> stack, List<Token> buffer, Set<Arc> arcSet);

    protected static Set<Action> getActions(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
        Set<Action> actions = new LinkedHashSet<>();
        if (buffer.size() == 0) {
            return actions;
        }
        int stackSize = stack.size();
        if (stackSize > 0) {
            boolean canLeft = true;
            boolean canReduce = false;
            Token sLast = stack.get(stackSize - 1);
            if (sLast.isRoot()) {
                canLeft = false;
            }
            for (Arc arc : arcSet) {
                if (sLast.id == arc.dependent) {
                    canLeft = false;
                    canReduce = true;
                    break;
                }
            }
            if (canLeft) {
                actions.add(Action.LEFT);
            }
            actions.add(Action.RIGHT);
            if (canReduce) {
                actions.add(Action.REDUCE);
            }
        }
        actions.add(Action.SHIFT);
        return actions;
    }
}
