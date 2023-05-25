package com.gitegg.platform.base.util;

/**
 * 规则：
 * 1、长度大于8，且小于32
 * 2、不能包含用户名
 * 3、不能包含连续3位及以上相同字母或数字
 * 4、不能包含3个及以上字典连续字符
 * 4、不能包含3个及以上键盘连续字符
 * 4、数字、小写字母、大写字母、特殊字符，至少包含三种
 */
public class CheckPassword {
    /**
     * 数字
     */
    private static final String REG_NUMBER = ".*\\d+.*";
    /**
     * 小写字母
     */
    private static final String REG_UPPERCASE = ".*[A-Z]+.*";
    /**
     * 大写字母
     */
    private static final String REG_LOWERCASE = ".*[a-z]+.*";
    /**
     * 特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)
     */
    private static final String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";
    /**
     * 键盘字符表(小写)
     * 非shift键盘字符表
     */
    private static final char[][] CHAR_TABLE1 = new char[][]{
            {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/', '\0', '\0', '\0'}};
    /**
     * shift键盘的字符表
     */
    private static final char[][] CHAR_TABLE2 = new char[][]{
            {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '{', '}', '|'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':', '"', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', '<', '>', '?', '\0', '\0', '\0'}};


    /**
     * 校验密码
     *
     * @param password 密码
     * @param username 用户名
     */
    public static String checkPasswordRule(String password, String username) {
        if (MyStringUtils.isEmpty(password) || password.length() < 8 || password.length() > 32) {
            return "长度小于8，或大于32";
        }
        if (password.contains(username)) {
            return "包含用户名";
        }
        if (isContinuousChar(password)) {
            return "包含3个及以上相同或字典连续字符";
        }
        if (isKeyBoardContinuousChar(password)) {
            return "包含3个及以上键盘连续字符";
        }
        int i = 0;
        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE)) i++;
        if (password.matches(REG_UPPERCASE)) i++;
        if (password.matches(REG_SYMBOL)) i++;
        if (i < 4) {
            return "密码必须包含大写字母、小写字母、数字、特殊字符";
        }
        return "OK";
    }


    /**
     * 是否包含3个及以上相同或字典连续字符
     */
    private static boolean isContinuousChar(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            int n1 = chars[i];
            int n2 = chars[i + 1];
            int n3 = chars[i + 2];
            // 判断重复字符
            if (n1 == n2 && n1 == n3) {
                return true;
            }
            // 判断连续字符： 正序 + 倒序
            if ((n1 + 1 == n2 && n1 + 2 == n3) || (n1 - 1 == n2 && n1 - 2 == n3)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含3个及以上键盘连续字符
     *
     * @param password 待匹配的字符串
     */
    private static boolean isKeyBoardContinuousChar(String password) {
        if (MyStringUtils.isEmpty(password)) {
            return false;
        }
        //考虑大小写，都转换成小写字母
        char[] lpStrChars = password.toLowerCase().toCharArray();

        // 获取字符串长度
        int nStrLen = lpStrChars.length;
        // 定义位置数组：row - 行，col - column 列
        int[] pRowCharPos = new int[nStrLen];
        int[] pColCharPos = new int[nStrLen];
        for (int i = 0; i < nStrLen; i++) {
            char chLower = lpStrChars[i];
            pColCharPos[i] = -1;
            // 检索在表1中的位置，构建位置数组
            for (int nRowTable1Idx = 0; nRowTable1Idx < 4; nRowTable1Idx++) {
                for (int nColTable1Idx = 0; nColTable1Idx < 13; nColTable1Idx++) {
                    if (chLower == CHAR_TABLE1[nRowTable1Idx][nColTable1Idx]) {
                        pRowCharPos[i] = nRowTable1Idx;
                        pColCharPos[i] = nColTable1Idx;
                    }
                }
            }
            // 在表1中没找到，到表二中去找，找到则continue
            if (pColCharPos[i] >= 0) {
                continue;
            }
            // 检索在表2中的位置，构建位置数组
            for (int nRowTable2Idx = 0; nRowTable2Idx < 4; nRowTable2Idx++) {
                for (int nColTable2Idx = 0; nColTable2Idx < 13; nColTable2Idx++) {
                    if (chLower == CHAR_TABLE2[nRowTable2Idx][nColTable2Idx]) {
                        pRowCharPos[i] = nRowTable2Idx;
                        pColCharPos[i] = nColTable2Idx;
                    }
                }
            }
        }

        // 匹配坐标连线
        for (int j = 1; j <= nStrLen - 2; j++) {
            //同一行
            if (pRowCharPos[j - 1] == pRowCharPos[j] && pRowCharPos[j] == pRowCharPos[j + 1]) {
                // 键盘行正向连续（asd）或者键盘行反向连续（dsa）
                if ((pColCharPos[j - 1] + 1 == pColCharPos[j] && pColCharPos[j] + 1 == pColCharPos[j + 1]) ||
                        (pColCharPos[j + 1] + 1 == pColCharPos[j] && pColCharPos[j] + 1 == pColCharPos[j - 1])) {
                    return true;
                }
            }
            //同一列
            if (pColCharPos[j - 1] == pColCharPos[j] && pColCharPos[j] == pColCharPos[j + 1]) {
                //键盘列连续（qaz）或者键盘列反向连续（zaq）
                if ((pRowCharPos[j - 1] + 1 == pRowCharPos[j] && pRowCharPos[j] + 1 == pRowCharPos[j + 1]) ||
                        (pRowCharPos[j - 1] - 1 == pRowCharPos[j] && pRowCharPos[j] - 1 == pRowCharPos[j + 1])) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
//        String password = "qazfW852";
//        System.out.println(checkPasswordRule(password, " "));
//        System.out.println(checkPasswordRule("Aa@123456.", " "));
//        System.out.println(checkPasswordRule("qwer46891.", " "));
//        System.out.println(checkPasswordRule("111q46891.", " "));
//        System.out.println(checkPasswordRule("edfq46891.", " "));
//        System.out.println(checkPasswordRule("Abnq46891.", " "));
    }
}
