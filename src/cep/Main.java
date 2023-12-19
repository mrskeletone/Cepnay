package cep;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static   double Tt=111.17;
    public static   double nt=93.15;
    public  static    double icp=2;
    public  static    double Kp=2.2;
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        double z1=  (29-2*icp);
        if(z1<13){
            System.out.println("Ошибка в передаточном отношении");
            return;
        }
        int[] zarr=new int[]{13,15,17,19,21,23,25,29};//сделать проверку
        z1=getZ1(z1,zarr);
        if(z1==-1){
            System.out.println("Не найдено кол-во зубьев из ряда");
            return;
        }
        System.out.println("z1="+z1);
        double z2=z1*icp;
        if(z1>120){
            System.out.println("Ошибка в передаточном отношении");
            return;
        }
        z2=Math.floor(z2);
        System.out.println("z2="+z2);
        double Pc=12.8*Math.pow(Tt/z1, (double) 1 /3);
        List<TablePc> tablePcList=TablePc.add();
        TablePc PcTrue;
        int flag = getFlag(tablePcList, Pc);
        if(flag==-1){
            System.out.println("Ошибка Pc");
            return;
        }
        PcTrue=tablePcList.get(flag);
        System.out.println("Pc="+PcTrue.getPc()+" N1p= "+PcTrue.getN1p()+" N1lim= "+PcTrue.getN1lim());
        TableCep tableCep=TableCep.find(PcTrue);
        if(tableCep==null){
            System.out.println("Нет такого госта");
            return;
        }
        System.out.println("Цепь="+tableCep.getGost());
        double Kэ,Kд = 0,Ka=0,Kн=0,Kрег=0,Kсм=0,Kреж=0,Kт=0;
        System.out.println("Номер нагрузки\n"+"1:Равномерная\n"+"2:Переменная\n"+"3:При сильныйх ударах");
        int s=scanner.nextInt();
        int nagr=s;
        switch (s){
            case 1:Kд=1;break;
            case 2:Kд=1.35;break;
            case 3:Kд=1.8;break;
        }
        Ka=1;
        System.out.println("Ваш Угол ");
        s=scanner.nextInt();
        double degree=s;
        if (s<45){
            Kн=1;
        }else {
            Kн=0.15*Math.sqrt(s);
        }
        System.out.println("Номер передач\n"+"1:Для передач с регулировкой положения оси одной из звездочек\n"+
                "2:Для передач c оттяжными звездочками или нажимными роликами\n"+"3:Для нерягулируемых передач ");
        s=scanner.nextInt();
        switch (s){
            case 1:Kрег=1;break;
            case 2:Kрег=1.1;break;
            case 3:Kрег=1.25;break;
        }
        System.out.println("Характер смазывания\n"+"1:Непрерывный в маслянной ванне или от насоса\n"+
                "2:Регулярный капельный или внутришарнирный\n"+"3:Нерегулярный");
        s=scanner.nextInt();
        switch (s){
            case 1:Kсм=0.8;break;
            case 2:Kсм=1;break;
            case 3:Kсм=1.5;break;
        }
        System.out.println("Режим работы\n"+"1:Односменный\n"+"2:Двухсменный\n"+"3:Трехсменный");
        s=scanner.nextInt();
        switch (s){
            case 1:Kреж=1;break;
            case 2:Kреж=1.25;break;
            case 3:Kреж=1.45;break;
        }
        System.out.println("Температура окружающей среды\n"+"1:от -25 до 150 градусов\n"+"2:при экстремальный температурах");
        s=scanner.nextInt();
        switch (s){
            case 1:Kт=1;break;
            case 2:Kт=2;break;
        }
        Kэ=Ka*Kд*Kн*Kт*Kрег*Kреж*Kсм;
        System.out.println("Кэ="+Kэ);

        double sinZ1 = Math.sin(Math.toRadians(180) / z1);
        double d1=PcTrue.getPc()/ sinZ1;
        System.out.println("d1="+d1);
        double Ft=2*Tt*Math.pow(10,3)/d1;
        System.out.println("Ft="+Ft);
        double Kряд=0;
        System.out.println("Выберите\n"+"1:Однорядная цепь\n"+"2:Двухрядная цепь\n"+"3:Трехрядная цепь");
        s=scanner.nextInt();
        switch (s){
            case 1:Kряд=1;break;
            case 2: Kряд=15.1;break;
            case 3:Kряд=25.1;break;
        }

        double p=Kэ*Ft/(tableCep.getA()*Kряд);
        int n=getN(PcTrue);
        double pmax=getP(n,PcTrue);
        if(pmax<p){
            System.out.println("p>[p]");
            return;
        }
        System.out.println("p="+p+"<="+"[p]="+pmax);
        double S=tableCep.getFp()*Math.pow(10,3)/(Kд*Ft);
        if(S<15){
            System.out.println("Ошибка S");
            return;
        }
        System.out.println("S="+S);
        double sinZ2 = Math.sin(Math.toRadians(180) / z2);
        double d2=PcTrue.getPc()/ sinZ2;
        System.out.println("d2="+d2);
        double cosZ1=Math.cos(Math.toRadians(180)/z1);
        double cosZ2=Math.cos(Math.toRadians(180)/z2);
        double da1=PcTrue.getPc()*(0.5+(cosZ1/ sinZ1));
        double da2=PcTrue.getPc()*(0.5+(cosZ2/ sinZ2));
        System.out.println("da1="+da1+" da2="+da2);
        double df1=d1-(tableCep.getDp()+0.1),df2=d2-(tableCep.getDp()+0.1);
        System.out.println("df1="+df1+"  df2="+df2);
        double b=0.9* tableCep.getBbh()-0.15;
        System.out.println("b="+b);
        double a=40*PcTrue.getPc();
        System.out.println("a="+a);
        double Lp=(2*a/PcTrue.getPc())+((z1+z2)/2)+Math.pow((z2-z1)/(2*3.14),2)*PcTrue.getPc()/a;
        Lp=Math.round(Lp) ;
        System.out.println("Lp="+Lp);
        a=(PcTrue.getPc())/4
                *(
                        Lp-(z2+z1)/2
                                +Math.sqrt(
                                        Math.pow(Lp-(z2+z1)/2,2)
                                                -8*
                                                Math.pow(
                                                        (z2-z1)/(2*3.14)
                                                        ,2)));
        System.out.println("a="+a);
        a=a-a*0.003;
        System.out.println("a="+a);
        double Kb;
        if(degree<=45){
            Kb=1.15;
        }else {
            Kb=1.05;
        }
        if(nagr==2){

        }
        Kb=Kb*1.1;
        double Fz=Kb*Ft;
        System.out.println("Fz="+Fz+" Kb="+Kb);
    }
    private static double getZ1(double z1,int[] zarr){
        for (int i = 0; i < zarr.length; i++) {
            if(z1<=zarr[i]&& Math.abs(z1-zarr[i])<2){
                return zarr[i];
            }
        }
        return -1;
    }
    private static int getFlag(List<TablePc> tablePcList, double Pc) {
        double raz=Double.MAX_VALUE;
        int flag=-1;
        for (int i = 0; i < tablePcList.size()-1; i++) {
            double h=Math.abs(tablePcList.get(i).getPc()- Pc);
            if(h<raz&& tablePcList.get(i).getN1p()>=nt && tablePcList.get(i)
                    .getPc()> Pc){
                raz=h;
                flag=i;
            }
        }
        if(flag==-1){
            for (int i = 0; i < tablePcList.size()-1; i++) {
                double h=Math.abs(tablePcList.get(i).getPc()- Pc);
                if(h<raz&& tablePcList.get(i).getN1p()>=nt ){
                    raz=h;
                    flag=i;
                }
            }
        }
        return flag;
    }
    private static int getN(TablePc pc){
        int[] n01=new int[]{50,200,400,600,800,1000,1200,1600,2800};
        double raz=Double.MAX_VALUE;
        int j=-1;
        for (int i = 0; i < n01.length; i++) {
            double h=Math.abs(n01[i]- pc.getN1p());
            if(h<=raz){
                raz=h;
                j=i;
            }
        }
        return j;
    }
    private static double getP(int n,TablePc pc){
        double[] arr=null;
        if(pc.getPc()>=12.7&&pc.getPc()<=15.875){
            arr=new double[]{35,31.5,28.5,26,24,22.5,20,18.5,14};
        } else if (pc.getPc()>=19.05&&pc.getPc()<=25.4) {
            arr=new double[]{35,30,26,23.5,21,19,17.5,15};
        } else if (pc.getPc()>=31.75&& pc.getPc()<=38.1) {
            arr=new double[]{35,29,24,21,18.5,16.5,15};
        }else {
            arr=new double[]{35,26,21,17.5,15};
        }
        double pmax=arr[n];
        return pmax;
    }
}
class  TablePc{
    private double Pc;
    private double n1p;
    private double n1lim;
    static List<TablePc> add(){
        List<TablePc> list=new ArrayList<>();
        list.add(new TablePc(12.7,1250,3100));
        list.add(new TablePc(15.875,1000,2300));
        list.add(new TablePc(19.05,900,1800));
        list.add(new TablePc(25.4,800,1200));
        list.add(new TablePc(31.75,630,3000));
        list.add(new TablePc(38.1,500,900));
        list.add(new TablePc(44.45,400,600));
        list.add(new TablePc(50.8,300,450));
        return list;
    }

    public TablePc(double pc, double n1p, double n1lim) {
        Pc = pc;
        this.n1p = n1p;
        this.n1lim = n1lim;
    }

    public double getPc() {
        return Pc;
    }

    public void setPc(double pc) {
        Pc = pc;
    }

    public double getN1p() {
        return n1p;
    }

    public void setN1p(double n1p) {
        this.n1p = n1p;
    }

    public double getN1lim() {
        return n1lim;
    }

    public void setN1lim(double n1lim) {
        this.n1lim = n1lim;
    }
}
class TableCep{
    private String gost;
    private double Pc,d,dp,Bbh,A,Fp,q;
    public static final List<TableCep> TABLE_CEPS=new ArrayList<>();

    public TableCep(String gost, double pc, double d, double dp, double bbh, double a, double fp, double q) {
        this.gost = gost;
        Pc = pc;
        this.d = d;
        this.dp = dp;
        Bbh = bbh;
        A = a;
        Fp = fp;
        this.q = q;
    }
    static {
        TABLE_CEPS.add(new TableCep("ПР-12,7-900-1",12.7,3.66,7.75,2.4,17.9,9,0.3));
        TABLE_CEPS.add(new TableCep("ПР-12,7-900-2",12.7,3.66,7.75,3.3,21,9,0.35));
        TABLE_CEPS.add(new TableCep("ПР-12,7-1820-1",12.7,4.45,8.51,5.4,40,18.2,0.65));
        TABLE_CEPS.add(new TableCep("ПР-12,7-1820-2*",12.7,4.45,8.51,7.75,50,18.2,0.75));
        TABLE_CEPS.add(new TableCep("ПР-15,875-2270-1",15.875,5.08,10.16,6.48,55,22.7,0.8));
        TABLE_CEPS.add(new TableCep("ПР-15,875-2270-2",15.875,5.08,10.16,9.65,71,22.7,1));
        TABLE_CEPS.add(new TableCep("ПР-19,05-3180",19.05,5.96,11.91,12.7,105,31.8,1.9));

        TABLE_CEPS.add(new TableCep("ПР-25,4-5670",25.4,7.95,15.88,15.88,180,56.7,2.6));
        TABLE_CEPS.add(new TableCep("ПР-31,75-8850",31.75,9.55,19.05,19.05,260,88.5,3.8));
        TABLE_CEPS.add(new TableCep("ПР-38,1-12700",38.1,11.1,22.23,25.4,395,127.0,5.5));
        TABLE_CEPS.add(new TableCep("ПР-44,45-17240",44.45,12.7,25.70,25.4,475,172.4,7.5));
        TABLE_CEPS.add(new TableCep("ПР-50,8-22680",50.8,14.29,28.58,31.75,645,226.8,9.7));

    }
    public static TableCep find(TablePc Pc){
        for (var i :
                TABLE_CEPS) {
            if(i.getPc()== Pc.getPc()){
                return i;
            }
        }
        return null;
    }

    public String getGost() {
        return gost;
    }

    public void setGost(String gost) {
        this.gost = gost;
    }

    public double getPc() {
        return Pc;
    }

    public void setPc(double pc) {
        Pc = pc;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getDp() {
        return dp;
    }

    public void setDp(double dp) {
        this.dp = dp;
    }

    public double getBbh() {
        return Bbh;
    }

    public void setBbh(double bbh) {
        Bbh = bbh;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getFp() {
        return Fp;
    }

    public void setFp(double fp) {
        Fp = fp;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }
}