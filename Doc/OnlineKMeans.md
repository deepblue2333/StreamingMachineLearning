```markdown
# Online K-Means Algorithm (Streaming ML)

## 1. Symbol Definition
| Symbol          | Description                          |
|-----------------|--------------------------------------|
| `K`             | Number of clusters                  |
| `μ_j(t)`        | Center of cluster j at step t        |
| `n_j(t)`        | Sample count of cluster j at step t |
| `x`             | New data point (D-dimensional)      |
| `η_j`           | Learning rate: `η_j = 1/(n_j+1)`    |

---

## 2. Algorithm Workflow

### 2.1 Initialization
```python
# Pseudocode
for j in 1..K:
    μ_j(0) = random_vector(0, 10)  # Random initialization
    n_j(0) = 0
```

---

### 2.2 Online Update (for new point x)
#### Step 1: Find Nearest Cluster
```python
distances = [euclidean_distance(x, μ_j) for j in 1..K]
j_star = argmin(distances)
```

#### Step 2: Update Cluster Center
```python
n_j_star += 1
η = 1 / n_j_star
μ_j_star = μ_j_star + η * (x - μ_j_star)
```

---

## 3. Mathematical Formulation

### 3.1 Euclidean Distance
```
d_j = sqrt(Σ_{i=1}^D (x_i - μ_ji)^2)
```

### 3.2 Learning Rule
```
μ_j(t+1) = {
    μ_j(t) + η_j*(x - μ_j(t))  if j = j_star,
    μ_j(t)                     otherwise
}
```
Where `η_j = 1/(n_j + 1)`

---

## 4. Visualization (Text-based)
```
Data Flow:
New Point → Calculate Distances → Select Cluster → Update Center
           ↑_________________________|__________________________|
```

---

## 5. Key Properties
- **Memory Efficient**: Only stores cluster centers (O(K) space)
- **Single-Pass Learning**: Processes each data point once
- **Adaptive Learning Rate**: `η_j` decreases as `n_j` increases

---

## 6. GitHub-Compatible Implementation
See full Java code in [online_kmeans.java](#) (hypothetical link to file)

> **Note**: For LaTeX rendering on GitHub, consider using browser extensions like [MathJax Plugin for GitHub](https://chrome.google.com/webstore/detail/mathjax-plugin-for-github/ioemnmodlmafdkllaclgeombjnmnbima).
``` 

### GitHub适配说明：
1. **代码块语法**：使用python伪代码块实现数学过程的高亮显示
2. **公式表示**：采用纯文本格式公式（兼容所有Markdown渲染器）
3. **可视化替代**：用ASCII文本流程图替代Mermaid
4. **符号表格**：使用GFM基础表格语法
5. **文件链接**：示例代码文件链接（需替换为实际文件路径）