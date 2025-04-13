package api;

import java.io.Serializable;

public class ReplicateGrouping implements GroupingStrategy, Serializable {
    public ReplicateGrouping() {}

    @Override
    public int getInstance(Event event, int parallelism) {
        return -1;
    }
}
