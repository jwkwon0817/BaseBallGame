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

    // initSetting
    private static String regex;
    private static String limitTry; // 0 1 4 5 7 8 9

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

        // Main game while loop
        Game: while (!isStop) {
            try {
                strikes = 0;
                balls = 0;
                outs = 0;
                isInRange = true;

                // Input message
                System.out.print("숫자 >>> ");
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
                    if (strikes == 3) {
                        System.out.println("삼진! 정답: " + chosenList);
                        judgeRank();
                        System.out.println("다시 플레이 하실건가요?\n");

                        while (true) {
                            System.out.print("\n다시 플레이 >>> ");
                            String replayStr = scanner.nextLine();

                            if (replayStr.equalsIgnoreCase("y")) {
                                System.out.println("\n[ 게임이 다시 시작됩니다. ]\n");
                                numList = new ArrayList<>(){{
                                    for (int i = 0; i <= 9; i++) {
                                        add(i);
                                    }
                                }};

                                // Choose numbers
                                chosenList = new ArrayList<>();
                                for (int i = 0; i < 3; i++) {
                                    Collections.shuffle(numList);
                                    chosenList.add(numList.get(0));
                                    numList.remove(0); // 0 2 8 9
                                }
                                tryCounts = 0;
                                break;
                            } else if (replayStr.equalsIgnoreCase("n")) {
                                isStop = false;
                                break Game;
                            } else {
                                System.out.println("\n[Y/N] 중에서 입력해주세요.\n");
                            }
                        }
                    } else if (outs == 3) {
                        System.out.println("3 아웃!\n");
                    } else if (!isDupl){
                        System.out.println(strikes + " 스트라이크 " + balls + " 볼\n");
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
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        System.out.println("\n[ 게임이 종료되었습니다. ]");
    }

    /** A method that judge the number is in range */
    public static boolean isRange(int num) {
        return (num >= 0) && (num <= 9);
    }

    /** A method that set initial game settings. */
    public static void initSetting() {
        regex = " "; // split(regex)
        limitTry = ""; // "" == infinity
    }

    /** A method that create a line */
    public static void createLine() {
        System.out.println("\n--------------------------------------------\n");
    }

    /** A method that judge rank when game finished */
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
        createLine();
        System.out.println("등급: " + rank);
        System.out.println(tryCounts + "번만에 맞췄습니다.");
        createLine();
    }

    /** A method that send rules to player */
    public static void sendRules() {
        System.out.println("[ 게임 규칙 ]");
        System.out.println("1. 숫자는 \"" + regex + "\"로 구분한다.");
        System.out.println("2. 숫자가 같고 위치까지 같으면 스트라이크");
        System.out.println("3. 숫자가 같지만 위치는 다르다면 볼");
        System.out.println("4. 숫자가 다르다면 아웃");
        createLine();
        System.out.println("현재 구분자: \"" + regex + "\"");
        createLine();
    }
}