node_attr_ara_expression <- read.delim("~/git/PPI-Predictions/R/node_attr_ara_expression.tab")

#temp_data <- subset(node_attr_ara_expression, node_attr_ara_expression$Degree < 50)

# Degree vs. Expression
plot(node_attr_ara_expression$Degree, node_attr_ara_expression$Exp)
lines(lowess(node_attr_ara_expression$Degree, node_attr_ara_expression$Exp), col="red")

  # Inverse relationship?
plot(node_attr_ara_expression$Degree, log(node_attr_ara_expression$Exp))
plot(log(node_attr_ara_expression$Degree), node_attr_ara_expression$Exp)
  # Power law relationship?
plot(log(node_attr_ara_expression$Degree), log(node_attr_ara_expression$Exp))

# Expression vs. Avg
plot(node_attr_ara_expression$Exp, node_attr_ara_expression$Avg)
lines(lowess(node_attr_ara_expression$Exp, node_attr_ara_expression$Avg), col="red")

# Inverse relationship?
plot(node_attr_ara_expression$Exp, log(node_attr_ara_expression$Avg))
plot(log(node_attr_ara_expression$Exp), node_attr_ara_expression$Avg)
# Power law relationship?
plot(log(node_attr_ara_expression$Exp), log(node_attr_ara_expression$Avg))