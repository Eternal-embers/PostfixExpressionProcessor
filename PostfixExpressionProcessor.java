import java.util.*;

public class PostfixExpressionProcessor implements Function {

    private static final Map<Character, Integer> opLevel = new HashMap<>();
    static{
        opLevel.put('(', 0);//左括号,保证左括号不会提前从栈中弹出
        opLevel.put('+', 2);//加法运算符
        opLevel.put('-', 2);//减法运算符
        opLevel.put('*', 3);//乘法运算符
        opLevel.put('/', 3);//除法运算符
        opLevel.put('%', 3);//取模运算符
        opLevel.put('^', 4);//幂运算符
        opLevel.put('!', 5);//阶乘符
        opLevel.put('~', 6);//负号
    }

    private static final Map<String, Integer> f = new HashMap<>();
    static{
        f.put("sh",1);//双曲正弦
        f.put("ch",1);//双曲余弦
        f.put("th",1);//双曲正切
        f.put("lg",1);//以10为底的对数
        f.put("ln",1);//自然对数
        f.put("sin", 1);//正弦函数
        f.put("cos", 1);//余弦函数
        f.put("tan", 1);//正切函数
        f.put("sec", 1);//正割函数
        f.put("csc", 1);//余割函数
        f.put("cot", 1);//余切函数
        f.put("exp", 1);//e的幂
        f.put("gcd",2);//最大公约数
        f.put("lcm",2);//最小公倍数
        f.put("log",2);//对数函数
        f.put("abs", 1);//绝对值
        f.put("max",2);//最大值
        f.put("min",2);//最小值
        f.put("perm",2);//排列数
        f.put("comb",2);//组合数
        f.put("pify",1);//转化为带π的式子
        f.put("frac", 1);//将有理数转化为分式
        f.put("sqrt",1);//平方根
        f.put("arsh",1);//反双曲正弦
        f.put("arch",1);//反双曲余弦
        f.put("arth",1);//反双曲正切
        f.put("ceil", 1);//向上取整
        f.put("floor", 1);//向下取整
        f.put("round", 1);//四舍五入
        f.put("prime", 1);//判断素数
        f.put("arcsin",1);//反三角正弦
        f.put("arccos",1);//反三角余弦
        f.put("arctan",1);//反三角正弦
        f.put("factor", 1);//素因子分解
    }

    public static int getLevel(Object obj){
        if(obj instanceof String)
            return 7;
        else if(obj instanceof Character){
            Integer level = opLevel.get(obj);
            if(level != null) return level;
        }
        return -1;
    }

    public List<Object> getPostfix(String expression) {
        Stack<Object> stk = new Stack<>();
        ArrayList<Object> postfix = new ArrayList<>();

        //清空空格和制表符
        expression = expression.replaceAll("\\s+", "");

        //兼容多种括号，(),（）,[]
        expression = expression.replaceAll("[（\\[]", "(");
        expression = expression.replaceAll("[）\\]]", ")");

        int length = expression.length();

        try {
            for (int i = 0; i < length; i++) {
                char ch = expression.charAt(i);

                if(Character.isLetter(ch)){
                    //获取此token
                    int k = i + 1;
                    while(k < length && Character.isLetter(expression.charAt(k))){
                        k++;
                    }
                    String token = expression.substring(i, k);


                    if(token.equals("PI") || token.equals("pi") )//常量π
                        postfix.add('π');
                    else if(token.equals("Eu"))//常量e
                        postfix.add('e');
                    else if(token.length() == 1)//用字母作为变量名
                        postfix.add(ch);
                    else //函数
                        stk.add(token);

                    i = k - 1;
                }

                /* 数值 */
                else if (ch >= '0' && ch <= '9') {
                    int k = i + 1;
                    while(k < length){
                        char c = expression.charAt(k);
                        if(c >= '0' && c <= '9' || c == '.') k++;
                        else break;
                    }

                    String sub = expression.substring(i, k);
                    try {
                        Double v = Double.parseDouble(sub);
                        postfix.add(v);
                    } catch (NumberFormatException ex) {
                        throw new Exception("[ERROR] 非法的数值: " + sub);
                    }

                    i = k - 1;
                }

                /* 括号 */
                else if(ch == '(')
                    stk.push(ch);
                else if(ch == ')') {
                    boolean match = false;//判断是否正确匹配到左括号
                    while(!stk.isEmpty()){
                        Object e = stk.peek();
                        if(e instanceof Character && e.equals('(')) {
                            match = true;
                            stk.pop();//弹出左括号
                            break;
                        }
                        else
                            postfix.add(stk.pop());
                    }

                    if(!match)
                        throw new Exception("[ERROR] 无效的表达式: " + expression + ", 缺少左括号");
                }

                /* 逗号,用于分隔参数 */
                else if(ch == ','){
                    //跳过逗号，逗号用于分隔参数
                }

                /* 运算符 */
                else{
                    int level = getLevel(ch);

                    //非法符号处理
                    if (level == -1)
                        throw new Exception("出现无效的运算符: " + ch);

                    //处理合法符号
                    while(!stk.isEmpty() && level <= getLevel(stk.peek())) {//栈不为空且ch的优先级小于等于栈顶元素
                            postfix.add(stk.pop());//将优先级高的运算添加到后缀表达式中
                    }
                    stk.push(ch);
                }
            }

            /* 将栈中剩余符号pop */
            while(!stk.isEmpty()) {
                Object e = stk.pop();
                if(e.equals('('))
                    throw new Exception("[ERROR] 无效的表达式: " + expression + ", 缺少右括号");
                postfix.add(e);
            }
        } catch(Exception ex){
            System.err.println(ex.getMessage());
        }

        return postfix;
    }

    /**
     * 计算后缀表达式的值
     * @param postfix 后缀表达式
     * @return 计算结果
     */
    public String evalPostfix(List<Object> postfix) {
        Stack<Double> values = new Stack<>();

        String result = "";

        try {
            for (Object e : postfix) {
                //数值
                if (e instanceof Double)
                    values.push((Double) e);

                //普通运算符，+、-、*、/、%、^、!、~
                if (e instanceof Character) {
                    char op = (char) e;//运算符

                    //常量处理
                    if(op == 'π')
                        values.push(Math.PI);
                    else if(op == 'e')
                        values.push(Math.E);

                    //单目运算符
                    else if(op == '~')
                        values.push(-values.pop());
                    else if(op == '!'){
                        double value = (double) Function.factorial(values.pop().intValue());
                        values.push(value);
                    }

                    //双目运算符
                    else {
                        double value1, value2;
                        double res = 0;

                        // 弹出两个操作数
                        try {
                            value2 = values.pop();
                            value1 = values.pop();
                        } catch(Exception ex){
                            System.err.printf("[ERROR] 运算符 '%c' 缺少操作数\n", op);
                            return null;
                        }

                        switch (op) {
                            case '+' -> res = value1 + value2;
                            case '-' -> res = value1 - value2;
                            case '*' -> res = value1 * value2;
                            case '/' -> {
                                res = value1 / value2;
                                if(value2 == 0){
                                    System.err.printf("[ERROR] 除数不能为0，运算 %f / %f 无法计算\n", value1, value2);
                                    return null;
                                }
                            }
                            case '%' -> {
                                int value1_int = (int) value1;
                                int value2_int = (int) value2;
                                try {
                                    res = value1_int % value2_int;
                                } catch (ArithmeticException ex) {
                                    System.err.printf("[ERROR] 模数不能为0，运算 %d %% %d 无法计算\n", value1_int, value2_int);
                                    return null;
                                }
                            }
                            case '^' -> res = Math.pow(value1, value2);
                        }
                        values.push(res);
                    }
                }

                //函数
                else if(e instanceof String func){
                    Integer argc = f.get(func);//参数个数

                     if(argc == null){
                         System.err.printf("[ERROR] 未知的函数：%s\n", func);
                         return null;
                     }

                     //获取函数参数
                     double[] args = new double[argc];
                     for(int i = argc - 1; i >= 0; i--){
                         try {
                             args[i] = values.pop();
                         } catch(Exception ex){
                             System.err.printf("[ERROR] 函数 %s 缺少参数\n", func);
                             return null;
                         }
                     }

                     double res = 0;
                     switch (func) {
                         case "sh" -> res = Math.sinh(args[0]);
                         case "ch" -> res = Math.cosh(args[0]);
                         case "th" -> res = Math.tanh(args[0]);
                         case "lg" -> res = Math.log10(args[0]);
                         case "ln" -> res = Math.log(args[0]);
                         case "sin" -> res = Math.sin(args[0]);
                         case "cos" -> res = Math.cos(args[0]);
                         case "tan" -> res = Math.tan(args[0]);
                         case "sec" -> res = 1/Math.cos(args[0]);
                         case "csc" -> res = 1/Math.sin(args[0]);
                         case "cot" -> res = 1/Math.tan(args[0]);
                         case "exp" -> res = Math.exp(args[0]);
                         case "gcd" -> res = Function.gcd((long) args[0], (long) args[1]);
                         case "lcm" -> res = Function.lcm((long) args[0], (long) args[1]);
                         case "log" -> res = Math.log(args[1]) / Math.log(args[0]);
                         case "abs" -> res = Math.abs(args[0]);
                         case "max" -> res = Math.max(args[0], args[1]);
                         case "min" -> res = Math.min(args[0], args[1]);
                         case "perm" -> res = Function.permutation((int) args[0], (int) args[1]);
                         case "comb" -> res = Function.combination((int) args[0], (int)args[1]);
                         case "pify" -> {return Function.pify(args[0]);}
                         case "frac" -> {return Function.convertToFraction(args[0]);}
                         case "sqrt" -> res = Math.sqrt(args[0]);
                         case "arsh" -> res = Function.arsh(args[0]);
                         case "arch" -> res = Function.arch(args[0]);
                         case "arth" -> res = Function.arth(args[0]);
                         case "ceil" -> res = Math.ceil(args[0]);
                         case "floor" -> res = Math.floor(args[0]);
                         case "round" -> res = Math.round(args[0]);
                         case "prime" -> {return Function.isPrime((int) args[0]) ? "true" : "false";}
                         case "arcsh" -> res = Math.asin(args[0]);
                         case "arcch" -> res = Math.acos(args[0]);
                         case "arcth" -> res = Math.atan(args[0]);
                         case "factor" -> {return Function.getPrimeFactor((int) args[0]);}
                     }
                     values.push(res);
                }
            }
        } catch(Exception ex){
            System.err.println(ex.getMessage());
        }

        if(!values.isEmpty())
            result = values.pop().toString();

        return result;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        PostfixExpressionProcessor pep= new PostfixExpressionProcessor();
        while(true){
            String expression = scan.nextLine();
            ArrayList<Object> postfix = new ArrayList<>(pep.getPostfix(expression));
            System.out.println("postfix: " + postfix);
            String result = pep.evalPostfix(postfix);
            System.out.println("result: " +result);
        }
    }
}
