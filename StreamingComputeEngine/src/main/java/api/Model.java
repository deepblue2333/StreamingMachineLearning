package api;

import lombok.Getter;

public interface Model {
    MachineLearningRowEvent predict(MachineLearningRowEvent event);
    void update(MachineLearningRowEvent event);
}
