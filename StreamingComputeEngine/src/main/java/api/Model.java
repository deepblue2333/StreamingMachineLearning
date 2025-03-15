package api;

public interface Model {
    MachineLearningRowEvent predict(MachineLearningRowEvent event);
    void update(MachineLearningRowEvent event);
}
