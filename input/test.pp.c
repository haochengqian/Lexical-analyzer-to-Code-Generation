const int maxn = - 1e5 + 1 ; 
struct Edge { 
 int v , next ; 
 bool vis ; 
 } edge [ 150001 < < 1 ] ; 
int head [ maxn ] ; 
int E ; 
int x , y , n , m ; 
int Btype , Time ; 
int dfn [ maxn ] , low [ maxn ] , Belong [ maxn ] ; 
int st [ maxn ] , Top ; 
const int POW = - 20 ; 
int d [ maxn ] , p [ maxn ] [ 20 ] ; 
int Log [ maxn ] ; 
int a , b , c , aa , bb , cc , ab , ac , bc ; 
void add_edge ( int s , int t ) { 
 edge [ E ] .v = t ; 
 edge [ E ] .vis = 0 ; 
 edge [ E ] .next = head [ s ] ; 
 head [ s ] = E ++ ; 
 } 
int get_val ( ) { 
 int ret = 0 ; 
 char c ; 
 while ( ( c = getchar ( ) ) == ' ' || c == '\n' ) ; 
 ret = c - '0' ; 
 while ( ( c = getchar ( ) ) ! = ' ' && c ! = '\n' ) 
 ret = ret * 10 + c - '0' ; 
 return ret ; 
 } 
void dfs ( int s ) { 
 int i , t ; 
 st [ ++ Top ] = s ; 
 dfn [ s ] = low [ s ] = ++ Time ; 
 for ( i = head [ s ] ; i ! = - 1 ; i = edge [ i ] .next ) { 
 if ( edge [ i ] .vis ) 
 continue ; 
 edge [ i ] .vis = edge [ i ^ 1 ] .vis = 1 ; 
 t = edge [ i ] .v ; 
 if ( ! dfn [ t ] ) { 
 dfs ( t ) ; 
 low [ s ] = min ( low [ s ] , low [ t ] ) ; 
 } else 
 low [ s ] = min ( low [ s ] , dfn [ t ] ) ; 
 } 
 if ( dfn [ s ] == low [ s ] ) { 
 Btype ++ ; 
 do { 
 t = st [ Top -- ] ; 
 Belong [ t ] = Btype ; 
 } while ( t ! = s ) ; 
 } 
 } 
void BCC ( int n ) { 
 int i ; 
 Time = 0 ; 
 Btype = 0 ; 
 Top = 0 ; 
 memset ( dfn , 0 , sizeof ( int ) * ( n + 1 ) ) ; 
 for ( i = 1 ; i <= n ; i ++ ) 
 if ( ! dfn [ i ] ) 
 dfs ( i ) ; 
 } 
void dfs ( int u , int fa ) { 
 d [ u ] = d [ fa ] + 1 ; 
 p [ u ] [ 0 ] = fa ; 
 for ( int i = 1 ; i < POW ; i ++ ) 
 p [ u ] [ i ] = p [ p [ u ] [ i - 1 ] ] [ i - 1 ] ; 
 for ( int i = 0 ; i < ( int ) edges [ u ] .size ( ) ; i ++ ) { 
 if ( edges [ u ] [ i ] .first == fa ) continue ; 
 dfs ( edges [ u ] [ i ] .first , u ) ; 
 } 
 } 
int lca ( int a , int b ) { 
 if ( d [ a ] > d [ b ] ) 
 a ^= b , b ^= a , a ^= b ; 
 if ( d [ a ] < d [ b ] ) { 
 int del = d [ b ] - d [ a ] ; 
 for ( int i = 0 ; i < POW ; i ++ ) 
 if ( del & ( 1 < < i ) ) b = p [ b ] [ i ] ; 
 } 
 if ( a ! = b ) { 
 for ( int i = POW - 1 ; i >= 0 ; i -- ) 
 if ( p [ a ] [ i ] ! = p [ b ] [ i ] ) 
 a = p [ a ] [ i ] , b = p [ b ] [ i ] ; 
 a = p [ a ] [ 0 ] , b = p [ b ] [ 0 ] ; 
 } 
 return a ; 
 } 
inline int find ( int a , int k ) { 
 for ( int i = 0 ; i < POW ; i ++ ) 
 if ( k & ( 1 < < i ) ) a = p [ a ] [ i ] ; 
 return a ; 
 } 
inline bool inside ( int u , pii x ) { 
 for ( int i = 0 ; i < ( int ) edges [ u ] .size ( ) ; i ++ ) 
 if ( edges [ u ] [ i ] == x ) return 1 ; 
 return 0 ; 
 } 
int main ( ) { 
 int i , j ; 
 while ( ~ scanf ( "%d%d" , & n , & m ) ) { 
 E = 0 ; 
 memset ( head , - 1 , sizeof ( int ) * ( n + 1 ) ) ; 
 while ( m -- ) { 
 x = get_val ( ) ; y = get_val ( ) ; 
 add_edge ( x , y ) ; 
 add_edge ( y , x ) ; 
 } 
 BCC ( n ) ; 

 for ( i = 0 ; i <= n ; i ++ ) 
 edges [ i ] .clear ( ) ; 

 for ( i = 1 ; i <= n ; i ++ ) 
 for ( j = head [ i ] ; ~ j ; j = edge [ j ] .next ) { 
 int v = edge [ j ] .v ; 
 if ( Belong [ i ] ! = Belong [ v ] ) 
 edges [ Belong [ i ] ] .pb ( mp ( Belong [ v ] , v ) ) ; 
 } 

 dfs ( 1 , 0 ) ; 
 scanf ( "%d" , & m ) ; 
 while ( m -- ) { 
 a = get_val ( ) ; c = get_val ( ) ; b = get_val ( ) ; 


 if ( a == c ) { 
 if ( a == b ) 
 puts ( "Yes" ) ; 
 else 
 puts ( "No" ) ; 
 continue ; 
 } 
 if ( a == b || c == b ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 

 int aa = Belong [ a ] , bb = Belong [ b ] , cc = Belong [ c ] ; 


 if ( aa == bb && bb == cc ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 

 int ab = lca ( aa , bb ) , ac = lca ( aa , cc ) , bc = lca ( bb , cc ) ; 

 if ( aa == bb ) { 
 if ( ac == aa ) { 
 int t = find ( cc , d [ cc ] - d [ aa ] - 1 ) ; 
 if ( inside ( t , mp ( aa , a ) ) ) 
 puts ( "No" ) ; 
 else 
 puts ( "Yes" ) ; 
 continue ; 
 } else { 
 if ( inside ( p [ aa ] [ 0 ] , mp ( aa , a ) ) ) 
 puts ( "No" ) ; 
 else 
 puts ( "Yes" ) ; 
 continue ; 
 } 
 } 
 if ( bb == cc ) { 
 if ( ac == cc ) { 
 int t = find ( aa , d [ aa ] - d [ cc ] - 1 ) ; 
 if ( inside ( t , mp ( cc , c ) ) ) 
 puts ( "No" ) ; 
 else 
 puts ( "Yes" ) ; 
 continue ; 
 } else { 
 if ( inside ( p [ cc ] [ 0 ] , mp ( cc , c ) ) ) 
 puts ( "No" ) ; 
 else 
 puts ( "Yes" ) ; 
 continue ; 
 } 

 } 



 if ( ab == bb ) 
 x = find ( aa , d [ aa ] - d [ bb ] - 1 ) ; 
 else 
 x = p [ bb ] [ 0 ] ; 
 if ( bc == bb ) 
 y = find ( cc , d [ cc ] - d [ bb ] - 1 ) ; 
 else 
 y = p [ bb ] [ 0 ] ; 
 bool flag = 0 ; 
 int pos ; 
 for ( i = 0 ; i < ( int ) edges [ x ] .size ( ) ; i ++ ) { 
 if ( inside ( y , edges [ x ] [ i ] ) ) { 
 flag = 1 ; 
 pos = edges [ x ] [ i ] .second ; 
 break ; 
 } 
 } 
 if ( flag && pos ! = b ) { 
 puts ( "No" ) ; 
 continue ; 
 } 

 if ( ab == aa && bc == bb && ac == aa ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 
 if ( bc == cc && ab == bb && ac == cc ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 

 if ( ab == bb && ac == bc ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 
 if ( ac == ab && bc == bb ) { 
 puts ( "Yes" ) ; 
 continue ; 
 } 
 puts ( "No" ) ; 
 } 

 } 
 return 0 ; 
 } 
