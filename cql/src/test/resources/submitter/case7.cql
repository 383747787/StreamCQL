
--2014.02.14 讨论结果
--数据源查询中不带窗口。不允许进行CQL的聚合运算以及having，sort等过滤条件

--全局参数定义
--允许定义全局参数，也可以在datasource内部定义私有参数，私有参数会覆盖全局参数
SET "datasource.rdb.driver" = "org.postgresql.Driver";

--定义数据源
--数据库用户名和密码暂时采用明文，后期统一对所有的用户名和密码进行加密处理


--必须是testmodel，因为数据库连接不上
set "streaming.common.istestmodel"="true";

CREATE INPUT STREAM Stream_voice_call
(
 CallingNumber   STRING,
 CalledNumber  STRING,
 ThirdNumber   STRING,
 CallingIMSI   STRING,
 CalledIMSI   STRING,
 ThirdIMSI   STRING,
 CallingIMEI   STRING,
 CallingHome   STRING,
 CalledHome   STRING,
 CallingLac   INT,
 CallingCellID  INT,
 CalledLac   INT,
 CalledCellID  INT,
 MscCode    INT,
 BscCode    INT,
 CallTime   LONG,
 ConnTime   LONG,
 CallingNetType  STRING,
 VipFlag    STRING,
 CalledNetType  STRING,
 InitTime   LONG
)
     SERDE BinarySerDe
         PROPERTIES ("serde.binaryserde.attributeslength"="26,26,26,15,15,15,17,6,6,4,4,4,4,4,4,8,8,1,1,1,8")
     SOURCE 'com.huawei.streaming.cql.toolkits.operators.TCPServerInputOperator'
         PROPERTIES ("operator.tcpserver.port" = "7999","operator.tcpserver.fixedlength"="203");

CREATE DATASOURCE Rdb_viptable
 SOURCE 'com.huawei.streaming.datasource.RDBDataSource'
 PROPERTIES (
				"datasource.rdb.url" = "jdbc:postgresql://192.168.0.2:5432/stream",
				"datasource.rdb.username"="postgres",
				"datasource.rdb.password"="postgres"
				);	

CREATE OUTPUT STREAM Hance_vip				
(
 CallingNumber   STRING,
 CalledNumber  STRING,
 IsVip  BOOLEAN
)
     SERDE SimpleSerDe
         PROPERTIES ("serde.simpleserde.separator" = ",")
     SINK 'com.huawei.streaming.operator.outputstream.ConsolePrintOp'
         PROPERTIES("operator.consoleprint.frequence"="1");

--INSERT INTO Hance_vip SELECT s.CallingNumber, s.CalledNumber, (CASE rdb.id WHEN null THEN FALSE ELSE TRUE END) AS IsVip  FROM Stream_voice_call AS s,
INSERT INTO Hance_vip SELECT s.CallingNumber, s.CalledNumber, (CASE rdb.id WHEN null THEN FALSE ELSE TRUE END) AS IsVip  FROM Stream_voice_call AS s,
DATASOURCE Rdb_viptable
			[
			SCHEMA (id int,number String),
			QUERY("select * from viptable where number = '${s.CallingNumber}' ")
			] rdb 
;

submit application case7_hance;