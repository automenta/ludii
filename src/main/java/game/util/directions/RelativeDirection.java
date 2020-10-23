// 
// Decompiled by Procyon v0.5.36
// 

package game.util.directions;

import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;

import java.util.ArrayList;
import java.util.List;

public enum RelativeDirection implements Direction
{
    Forward(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            if (supportedDirections.contains(baseDirn)) {
                directions.add(baseDirn);
            }
            return directions;
        }
    }, 
    Backward(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            if (supportedDirections.contains(baseDirn.opposite())) {
                directions.add(baseDirn.opposite());
            }
            return directions;
        }
    }, 
    Rightward(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            if (supportedDirections.contains(baseDirn.rightward())) {
                directions.add(baseDirn.rightward());
            }
            return directions;
        }
    }, 
    Leftward(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            if (supportedDirections.contains(baseDirn.leftward())) {
                directions.add(baseDirn.leftward());
            }
            return directions;
        }
    }, 
    Forwards(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            for (DirectionFacing directionToAdd = baseDirn.leftward().right(); directionToAdd != baseDirn.rightward(); directionToAdd = directionToAdd.right()) {
                if (supportedDirections.contains(directionToAdd)) {
                    directions.add(directionToAdd);
                }
            }
            return directions;
        }
    }, 
    Backwards(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            for (DirectionFacing directionToAdd = baseDirn.opposite().leftward().right(); directionToAdd != baseDirn.opposite().rightward(); directionToAdd = directionToAdd.right()) {
                if (supportedDirections.contains(directionToAdd)) {
                    directions.add(directionToAdd);
                }
            }
            return directions;
        }
    }, 
    Rightwards(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            for (DirectionFacing directionToAdd = baseDirn.right(); directionToAdd != baseDirn.opposite(); directionToAdd = directionToAdd.right()) {
                if (supportedDirections.contains(directionToAdd)) {
                    directions.add(directionToAdd);
                }
            }
            return directions;
        }
    }, 
    Leftwards(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            for (DirectionFacing directionToAdd = baseDirn.left(); directionToAdd != baseDirn.opposite(); directionToAdd = directionToAdd.left()) {
                if (supportedDirections.contains(directionToAdd)) {
                    directions.add(directionToAdd);
                }
            }
            return directions;
        }
    }, 
    FL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.left(); !supportedDirections.contains(left); left = left.left()) {}
            directions.add(left);
            return directions;
        }
    }, 
    FLL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.left(); !supportedDirections.contains(left); left = left.left()) {}
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            directions.add(left);
            return directions;
        }
    }, 
    FLLL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.left(); !supportedDirections.contains(left); left = left.left()) {}
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            directions.add(left);
            return directions;
        }
    }, 
    BL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.opposite().left(); !supportedDirections.contains(left); left = left.left()) {}
            directions.add(left);
            return directions;
        }
    }, 
    BLL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.opposite().left(); !supportedDirections.contains(left); left = left.left()) {}
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            directions.add(left);
            return directions;
        }
    }, 
    BLLL(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing left;
            for (left = baseDirn.opposite().left(); !supportedDirections.contains(left); left = left.left()) {}
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            while (!supportedDirections.contains(left)) {
                left = left.left();
            }
            directions.add(left);
            return directions;
        }
    }, 
    FR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.right(); !supportedDirections.contains(right); right = right.right()) {}
            directions.add(right);
            return directions;
        }
    }, 
    FRR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.right(); !supportedDirections.contains(right); right = right.right()) {}
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            directions.add(right);
            return directions;
        }
    }, 
    FRRR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.right(); !supportedDirections.contains(right); right = right.right()) {}
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            directions.add(right);
            return directions;
        }
    }, 
    BR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.opposite().right(); !supportedDirections.contains(right); right = right.right()) {}
            directions.add(right);
            return directions;
        }
    }, 
    BRR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.opposite().right(); !supportedDirections.contains(right); right = right.right()) {}
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            directions.add(right);
            return directions;
        }
    }, 
    BRRR(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>(1);
            DirectionFacing right;
            for (right = baseDirn.opposite().right(); !supportedDirections.contains(right); right = right.right()) {}
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            while (!supportedDirections.contains(right)) {
                right = right.right();
            }
            directions.add(right);
            return directions;
        }
    }, 
    SameDirection(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            return directions;
        }
    }, 
    OppositeDirection(true) {
        @Override
        public List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections) {
            final List<DirectionFacing> directions = new ArrayList<>();
            return directions;
        }
    };
    
    public abstract List<DirectionFacing> directions(final DirectionFacing baseDirn, final List<DirectionFacing> supportedDirections);
    
    RelativeDirection(final boolean isAbsolute) {
    }
    
    @Override
    public DirectionsFunction directionsFunctions() {
        return new Directions(this, null, null, null);
    }
}
