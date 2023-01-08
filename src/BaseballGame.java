import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class BaseballGame {
    private static int strikes;
    private static int balls;
    private static int outs;
    private static int tryCounts = 0;
    private static int intLimitTry = 0;

    private static String rank = "";

    // boolean
    private static boolean isDupl = false;
    private static boolean isInRange = true;
    private static boolean isNum = true;
    private static boolean isInfinity = true;
    private static boolean isStop = false;
    private static boolean isReplay = true;

    // initSetting
    private static String regex;
    private static String limitTry;

    // Main method
    public static void main(String[] args) throws IOException  {
        // Create BufferedReader object
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        Scanner scanner = new Scanner(System.in);

        // Create default ArrayList
        List<Integer> numList = new ArrayList<>(){{
            for (int i = 0; i <= 9; i++) {
                add(i);
            }
        }};

        // Choose numbers
        List<Integer> chosenList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Collections.shuffle(numList);
            chosenList.add(numList.get(0));
            numList.remove(0);
        }



        initSetting();

        // Judge limitTry
        if (!limitTry.equals("")) {
            intLimitTry = Integer.parseInt(limitTry);
            isInfinity = false;
        } else {
            isInfinity = true;
        }

        sendRules();

        while (!isStop) {
            try {
                if (!isReplay) {
                    strikes = 0;
                }
                balls = 0;
                outs = 0;
                isInRange = true;

                // Input message
                System.out.print(">>> ");
                String userStr = bf.readLine();

                int[] userArr = Stream.of(userStr.split(regex)).mapToInt(Integer::parseInt).toArray();
                List<Integer> userList = new ArrayList<>();
                for (int i : userArr) {
                    userList.add(i);
                }

                // Judge numbers
                if (userArr.length != 3) {
                    isNum = false;
                } else {
                    isNum = true;
                }

                for (int i = 0; i < 3; i++) {
                    if (!isRange(userArr[i])) {
                        isInRange = false;
                        break;
                    }
                }

                if (!isNum) {
                    System.out.println("\n[ 오류 ] 숫자를 3개 입력해 주시기 바랍니다. (구분: \" \")\n");
                }

                if (!isInRange) {
                    System.out.println("\n[ 오류 ] 0 ~ 9 사이 숫자를 입력해 주시기 바랍니다.\n");
                }

                if (userList.size() != userList.stream().distinct().count()) {
                    System.out.println("\n[ 오류 ] 입력하신 숫자 중에 중복된 요소가 있습니다.\n");
                    isDupl = true;
                } else {
                    isDupl = false;
                }

                for (int i = 0; i < 3; i++) {
                    if (chosenList.contains(userList.get(i))) {
                        if (chosenList.get(i) == userList.get(i)) {
                            strikes += 1;
                        } else {
                            balls += 1;
                        }
                    } else {
                        outs += 1;
                    }
                }

                if (!isDupl && isInRange && isNum) {
                    // Add tryCounts
                    tryCounts += 1;

                    // Show try counts
                    if (!isInfinity) {
                        intLimitTry--;
                        System.out.println("남은 시도 횟수: " + intLimitTry);
                    }

                    // Game finished
                    if (strikes == 3 && isReplay) {
                        System.out.println("삼진! 정답: " + chosenList);
                        judgeRank();
                        System.out.print("더 하실건가요? [Y/N] >>> ");
                        String regameStr = scanner.next();

                        if (regameStr.equalsIgnoreCase("y")) {
                            isStop = false;
                        } else if(regameStr.equalsIgnoreCase("N")) {
                            isStop = true;
                            isReplay = false;
                        } else {
                            System.out.print("[Y/N] 중에 입력해주세요!");
                            isReplay = true;
                        }
                    } else if (outs == 3) {
                        System.out.println("3 아웃!");
                    } else if (!isDupl){
                        System.out.println(strikes + " Strike " + balls + " Ball\n");
                    }
                }

                // Game over because of tryCounts
                if (!isInfinity && intLimitTry == 0) {
                    System.out.println("[ 시도 가능한 횟수가 끝났습니다. 제한: " + limitTry + "]");
                    break;
                }
            } catch (NumberFormatException e) {             // Make player input 0 ~ 9 numbers.
                System.out.println("\n[ 오류 ] 0 ~ 9 사이 숫자를 입력해 주시기 바랍니다.\n");
            } catch (ArrayIndexOutOfBoundsException e) {    // Make player input 3 numbers.
                System.out.println("\n[ 오류 ] 숫자를 3개 입력해 주시기 바랍니다. (구분: \"" + regex + "\")\n");
            } catch (IndexOutOfBoundsException e) {
            }
        }

        System.out.println("\n[ 게임이 종료되었습니다. ]");
    }

    // Judge numbers are in range
    public static boolean isRange(int num) {
        return (num >= 0) && (num <= 9);
    }

    // Init settings
    public static void initSetting() { // 2 3 4 6 8 9 0 [ * * 3 ]
        regex = " "; // split(regex)
        limitTry = ""; // "" == infinity
    }

    // Simple create line method
    public static void createLine() {
        System.out.println("\n--------------------------------------------\n");
    }

    // Judge final ranks
    public static void judgeRank() {
        if (tryCounts == 1) {
            rank = "A+";
            System.out.println("[ !*경*! 한 번만에 맞추셨습니다! !*축*! ]");
        } else if (tryCounts >= 2 && tryCounts < 5) {
            rank = "A";
        } else if (tryCounts >= 5 && tryCounts < 10) {
            rank = "B";
        } else if (tryCounts >= 10 && tryCounts < 15) {
            rank = "C";
        } else if (tryCounts >= 15 && tryCounts < 20) {
            rank = "D";
        } else if (tryCounts >= 20 && tryCounts < 25) {
            rank = "E";
        } else {
            rank = "F";
        }
        System.out.println("등급: " + rank);
        System.out.println(tryCounts + "번만에 맞췄습니다.");
    }

    // Send rules to player
    public static void sendRules() {
        System.out.println("[ 게임 규칙 ]");
        System.out.println("1. 숫자는 \"" + regex + "\"로 구분한다.");
        System.out.println("2. 숫자가 같고 위치까지 같으면 STRIKE");
        System.out.println("3. 숫자가 같지만 위치는 다르다면 BALL");
        System.out.println("4. 숫자가 다르다면 OUT");
        createLine();
        System.out.println("현재 regex: \"" + regex + "\"");
        createLine();
    }
}
