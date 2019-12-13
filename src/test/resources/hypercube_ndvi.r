all_dim = names(dim(data))
ndvi_result = st_apply(data, FUN = function(X,...) {
			(X[8]-X[4])/(X[8]+X[4])
		}, MARGIN = all_dim[-which(all_dim=="band")])
ndvi_result 

