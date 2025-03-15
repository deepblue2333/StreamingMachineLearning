package api;

import lombok.Getter;

public abstract class TrainOperator extends Operator {
    @Getter
    private Model model;

    protected int instance;

    public TrainOperator(String name, int parallelism, Model model) {
        super(name, parallelism);
        this.model = model;
    }

    public TrainOperator(String name, int parallelism, GroupingStrategy grouping, Model model) {
        super(name, parallelism, grouping);
        this.model = model;
    }

    // 更新模型的方法
    public abstract void update(MachineLearningRowEvent event);

    // 进行预测的方法
    public abstract MachineLearningRowEvent predict(MachineLearningRowEvent event);

}
