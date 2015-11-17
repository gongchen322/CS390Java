<html>
<body>
<style>
.wrapper {
    text-align: center;
    
}
</style>
<div class="wrapper">
<img src="http://www.premiershipcoach.com.au/wp-content/uploads/2012/06/seek.jpg" alt="Mountain View" width=500 height:500 >

<form action="results.php" method="GET">
Query:<br>
<input type="text" name="query" >
<br><br>
<input type="submit" value="Submit">
</form> 
</div>

<?php
error_reporting(E_ALL);
$servername = "localhost";
$username = "root";
$password = "newpwd";
$dbname = "crawler";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
     die("Connection failed: " . $conn->connect_error);
}

if (isset($_GET['query'])) {
    $token = strtok($_GET['query'], " ");
    $a=0;
    $second = array();
    $first = array();
    while ($token !== false) {
        $sql = 'SELECT urlid FROM words WHERE word="'.$token.'"';
        $result = $conn->query($sql);
		$second=$first;
        $first = array();
        
        if ($result->num_rows > 0) {
             // output data of each row
             while($row = $result->fetch_assoc()) {
					array_push($first, $row["urlid"]);
             
				}
        }
		$a=$a+1;
        $token = strtok(" ");
    }
	if($a==1){
		$second=$first;
	}else{
		 $second= array_intersect($first, $second);
	}    

	foreach($second as $link){
		$sql='SELECT url, description, image FROM urls WHERE urlid= '.$link;
		$result = $conn->query($sql);
		if($result->num_rows > 0){
			while($row= $result ->fetch_assoc())
			{
				echo '<img src=" '.$row["image"].' "height="100" width="100"> <br>';
				echo '<a href=" ' .$row["url"].' ">'.$row["description"]. '</a> <br><br>';
				echo '<hr>';
			}
		}		
	}

}

$conn->close();
?> 

</body>
</html>