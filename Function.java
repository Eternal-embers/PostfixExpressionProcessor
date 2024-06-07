public interface Function {
    /**
     * 计算最大公约数
     * @return a和b的最大公约数
     */
    static long gcd(Long a, Long b) {
        if (a == 0 && b == 0) {
            System.err.println("gcd(0,0) 没有定义");
            return 0;
        }
        if (a == 0)
            return Math.abs(b);
        if (b == 0)
            return Math.abs(a);

        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    /**
     * 安全乘法
     * @return a和b的乘积，如果可能导致整数溢出，则抛出ArithmeticException
     */
    static long safeMultiply(long a, long b) throws ArithmeticException {
        // 检查乘法是否可能导致正数溢出
        if (a > 0 && b > 0 && a > (Long.MAX_VALUE / b)) {
            throw new ArithmeticException("正数溢出: " + a + " * " + b);
        }
        // 检查乘法是否可能导致负数溢出
        if (a < 0 && b < 0 && a < (Long.MIN_VALUE / b)) {
            throw new ArithmeticException("负数溢出: " + a + " * " + b);
        }
        // 如果没有溢出，执行乘法
        return a * b;
    }

    /**
     * 计算最小公倍数
     * @return a和b的最小公倍数
     */
    static long lcm(long a, long b){
        if(a == 0 && b == 0){
            System.err.println("lcm(0,0) 没有定义");
            return 0;
        }

        // 0与任何数的最小公倍数是那个非零数，但两个0的最小公倍数未定义
        if (a == 0 || b == 0) {
            return Math.max(Math.abs(a), Math.abs(b));
        }

        a = Math.abs(a);
        b = Math.abs(b);
        long gcd = gcd(a, b);

        return safeMultiply(a,b) / gcd;
    }

    /**
     * 排列数P(n, k)表示从n个元素中取出k个元素的排列数。排列数的定义为：P(n, k) = n! / (n-k)!。
     * @param n 元素总数
     * @param k 取出的元素个数
     * @return P(n, k)
     */
    static long permutation(int n, int k) {
        if (k > n) {
            return 0; // 当k大于n时，没有有效的排列
        } else if (k == 0 || k == n) {
            return 1; // 当k为0或n时，排列数为1
        } else {
            return factorial(n) / factorial(n - k);
        }
    }

    /**
     * 组合数C(n, k)表示从n个元素中取出k个元素的组合数。组合数的定义为：C(n, k) = n! / (k! * (n-k)!)。
     * @param n 元素总数
     * @param k 取出的元素个数
     * @return C(n, k)
     */
    static long combination(int n, int k) {
        if (k > n) {
            return 0; // 当k大于n时，没有有效的组合
        } else if (k == 0 || k == n) {
            return 1; // 当k为0或n时，组合数为1
        } else {
            // 计算组合数，使用公式 C(n, k) = n! / (k! * (n-k)!)
            long result = 1;
            for (int i = 0; i < k; i++) {
                result = result * (n - i);
            }
            for (int i = 1; i <= k; i++) {
                result = result / i;
            }
            return result;
        }
    }

    /**
     *  将一个数转为以π为因子的式子,例如pify(1.75*π) = π + (3/4)*π
     * @param num 要转换的数
     * @return 转换得到的带π的式子
     */
    static String pify(Double num){
        boolean ifPositive = true;//是否是正数
        double coef;//转换成带π的式子中π的系数

        if(num == 0) return "0";//如果num为0，返回空字符串
        if(num < 0) {
            num = -num;//取绝对值
            ifPositive = false;//判断正负
        }

        coef = num / Math.PI;
        int integerPart = (int)Math.floor(coef);//向下取整，整数部分
        double fractionalPart = coef - integerPart;//小数部分

        //正数
        if(ifPositive) {
            if (fractionalPart != 0) {
                if(integerPart != 0)
                    return (integerPart != 1 ? integerPart : "") + "π + (" + convertToFraction(fractionalPart) + ")π";
                else
                    return "(" + convertToFraction(fractionalPart) + ")π";
            }
            else
                return (integerPart != 1 ? integerPart : "") + "π";
        }

        //负数
        else {
            if (fractionalPart != 0)
                if(integerPart != 0)
                    return "-" + (integerPart != 1 ? integerPart : "") + "π - (" + convertToFraction(fractionalPart) + ")π)";
                else
                    return "-(" + convertToFraction(fractionalPart) + ")π";
            else
                return "-" + (integerPart != 1 ? integerPart : "") + "π";
        }
    }

    /**
     *
     * @param num 指定求pram的分式形式
     * @return 转换得到的分式字符串
     */
     static String convertToFraction(Double num){
        double EPSILON = 1e-15; // 定义一个极小值作为误差限制
         int maxDenominator = 1000000000; // 定义最大分母

        double error = Math.abs(num); // 初始化误差
        int bestNumerator = 0; // 存储最佳近似的分子
        int bestDenominator = 1; // 存储最佳近似的分母

        for (int i = 1; i <= maxDenominator; i++) {
            int currentNumerator = (int) Math.round(num * i); // 计算当前分子
            double currentError = Math.abs(num - (double) currentNumerator / i); // 计算当前误差

            if (currentError < error - EPSILON) {
                // 如果当前误差小于之前的误差，更新最佳近似的分子、分母和误差
                error = currentError;
                bestNumerator = currentNumerator;
                bestDenominator = i;
            }
        }

        return bestNumerator + "/" + bestDenominator;//返回转换得到的分式
    }

    /**
     * 计算阶乘
     * @param pram 阶乘参数
     * @return pram的阶乘
     */
    static long factorial(int pram){
        if(pram < 0)
            throw new IllegalArgumentException("[ERROR] 阶乘函数不能处理负数：" + pram);

        long res = 1;
        for(int i = 1; i <= pram; i++)
            res *= i;

        return res;
    }

    /**
     * 反双曲正弦函数,arsh = ln(x + sqrt(x ^ 2 + 1)),定义域(-∞,∞)，值域(-∞,∞)
     * @param x
     * @return
     */
    static double arsh(double x){
        return Math.log(x + Math.sqrt(x * x + 1));//arsh = ln(x + sqrt(x ^ 2 + 1));
    }

    /**
     * 反双曲余弦函数,arth = ln[(1+x)/(1-x)] / 2,定义域(-1,1)，值域(-∞,∞)
     * @param x
     * @return
     */
    static double arch(double x){
        return Math.log(x + Math.sqrt(x * x - 1));//arch = ln(x + sqrt(x ^ 2 - 1));
    }

    /**
     * 反三角正切函数, arth = ln[(1+x)/(1-x)] / 2,定义域[-1,1]，值域(-∞,∞)
     * @param x
     * @return
     */
    static double arth(double x){
        return 0.5 * Math.log((1 + x) / (1 - x));
    }

    /**
     * 判断一个数是否为质数
     * @param x
     * @return true表示x是质数，false表示x不是质数
     */
    static boolean isPrime(int x){
        if(x < 2) return false;

        for(int i = 2;i <= x / i;i++)
            if(x % i == 0) return false;

        return true;
    }

    /**
     * 获取一个数的质因数，返回形式为"2^3 * 3^2 * 5"
     * @param x 要获取质因数的数
     * @return x的质因数
     */
    static String getPrimeFactor(int x){
        StringBuilder sb = new StringBuilder();

        if(x < 0) sb.append("-1 * ");

        for(int i = 2;i <= x / i;i++){//只需要枚举sqrt(x)之前的质因数
            if(x % i == 0) {//i之前的质因数均被分解，因此i一定是质数
                int s = 0;
                while(x % i == 0){
                    x /= i;
                    s++;
                }

                if(s > 1)
                    sb.append(String.format("[%d^%d] * ", i, s));
                else
                    sb.append(i + " * ");
            }
        }

        if(x > 1)
            sb.append(x);

        return sb.toString();
    }
}
