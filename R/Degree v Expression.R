data_out_hc3 <- read.delim("~/Desktop/data_out_hc3.tab")
temp_data <- subset(data_out_hc3, data_out_hc3$Degree < 50)
plot(log(data_out_hc3$Exp), log(data_out_hc3$Avg))
lines(lowess(data_out_hc3$Degree, data_out_hc3$Avg), col="red")