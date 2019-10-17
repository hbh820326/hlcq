package com.exchange_rate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jcodecraeer.xrecyclerview.BaseRecyclerAdapter;
import com.jcodecraeer.xrecyclerview.BaseRecyclerHolder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExchangeRateActivity extends Activity {
    XRecyclerView listView;
    private List<LinkedTreeMap<String, String>> datas = new ArrayList<>();
    BaseRecyclerAdapter adapter;
    Button btn;
    boolean isBack;
    TextView content;
    Spinner spinner1;
    Spinner spinner2;
    AdapterView.OnItemSelectedListener ItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            getParitiesData();
          //  Log.e("Z","onItemSelected");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
         //   Log.e("Z","onNothingSelected");
        }
    };
    View.OnLongClickListener longllick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            isBack = !isBack;
            if (isBack) {
                btn.setText("单击进入狗带VT\n和讯银行(长按切换数据源)");
                getAllDate();//人民币网
            } else {
                btn.setText("单击进入狗带VT\n中国银行(长按切换数据源)");
                getBankData();//中国银行
            }
            return true;
        }
    };
    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String flag = (String) view.getTag();
            switch (flag) {
                case "http://www.boc.cn/sourcedb/whpj/"://中国银行外汇牌价
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(flag);
                    intent.setData(content_url);
                    startActivity(intent);

//                    Intent intent = new Intent(ExchangeRateActivity.this, BrowserActivity.class);
//                    intent.putExtra("url", flag);
//                    startActivity(intent);
                    break;
                case "http://www.vultr1.com/"://狗带TV
                case "http://www.goudaitv.cc/":
                    intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    content_url = Uri.parse(flag);
                    intent.setData(content_url);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(ExchangeRateActivity.this, flag, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((ExchangeRateApp) getApplication()).NetRequest("http://218.5.1.213:86/mobile/login/login.do?userCode=13599213618&userPwd=F991B9F52ED54510&equip=862012030696316&appType=android", 0, new ExchangeRateApp.IRequestCallback() {
//            @Override
//            public void success(Object str, int type) {
//                Log.e("Z","密码验证：返回值："+str);
//                ((ExchangeRateApp) getApplication()).NetRequest("http://218.5.1.213:86/mobile/im/queryUserInfo.do?userid=", 0, new ExchangeRateApp.IRequestCallback() {
//                    @Override
//                    public void success(Object str, int type) {
//                        Log.e("Z","取用户信息：返回值："+str);
//                    }
//                },"String");
//            }
//        },"String");

        setContentView(R.layout.exchange_rate_activity);
        listView = (XRecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager Linear = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//列表布局
        listView.setHasFixedSize(true);//提升性能（前提是chatListView的大小不能包裹内容）
        listView.setLayoutManager(Linear);
        View headView = View.inflate(this, R.layout.head_view, null);
        btn = (Button) headView.findViewById(R.id.list_data_control);
        btn.setOnClickListener(listener);
        btn.setOnLongClickListener(longllick);
      //  btn.setTag("http://www.boc.cn/sourcedb/whpj/");
      //  btn.setTag("http://www.vultr1.com/");
        btn.setTag( "http://www.goudaitv.cc/");


        content = (TextView) headView.findViewById(R.id.content);
        spinner1 = (Spinner) headView.findViewById(R.id.spinner1);
        spinner1.setAdapter(new BaseAdapter() {
            String[] languages = getResources().getStringArray(R.array.spingarr);

            @Override
            public int getCount() {
                return languages.length;
            }

            @Override
            public Object getItem(int position) {
                return languages[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.spinner_item, null);
                }
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(languages[position]);
                return convertView;
            }
        });
        spinner1.setOnItemSelectedListener(ItemSelected);
        spinner2 = (Spinner) headView.findViewById(R.id.spinner2);
        spinner2.setAdapter(new BaseAdapter() {
            String[] languages = getResources().getStringArray(R.array.spingarr);

            @Override
            public int getCount() {
                return languages.length;
            }

            @Override
            public Object getItem(int position) {
                return languages[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.spinner_item, null);
                }
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(languages[position]);
                return convertView;
            }
        });
        spinner2.setOnItemSelectedListener(ItemSelected);
        spinner2.setSelection(1, true);//美元
        listView.addHeaderView(headView);//添加头视图
        listView.setRefreshProgressStyle(22);//(-1   27)
        listView.setLoadingMoreProgressStyle(22);//
        adapter = new BaseRecyclerAdapter<LinkedTreeMap<String, String>>(datas, R.layout.ecxhange_currency_item, listener, null) {
            @Override
            public void convert(BaseRecyclerHolder holder, LinkedTreeMap<String, String> data) {
                holder.itemView.setTag("(" + data.get("bank") + ")" + data.get("currency") + ":" + data.get("cenPrice"));
                TextView name = holder.getView(R.id.name);
                TextView fBuyPri = holder.getView(R.id.fBuyPri);
                TextView mBuyPri = holder.getView(R.id.mBuyPri);
                TextView fSellPri = holder.getView(R.id.fSellPri);
                TextView mSellPri = holder.getView(R.id.mSellPri);
                TextView bankConversionPri = holder.getView(R.id.bankConversionPri);
                TextView time = holder.getView(R.id.time);
                SpannableString spannableString = new SpannableString(data.get("code") + data.get("currency") + "(" + data.get("bank") + ")");
                AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp14));
                spannableString.setSpan(
                        sizeSpan,
                        spannableString.length() - (data.get("bank").length() + 2),
                        spannableString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                name.setText(spannableString);
                fBuyPri.setText("现汇买入价: " + inspectString(data.get("buyPrice1")));
                mBuyPri.setText("现钞买入价: " + inspectString(data.get("buyPrice2")));
                mSellPri.setText("现钞卖出价: " + inspectString(data.get("sellPrice2")));
                fSellPri.setText("现汇卖出价: " + inspectString(data.get("sellPrice1")));
                bankConversionPri.setText("中间价:" + inspectString(data.get("cenPrice")));
                time.setText("信息发布时间：" + data.get("releasedate"));
            }
        };
        listView.setAdapter(adapter);
        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {//刷新监听
                if (isBack) getAllDate();//人民币网
                else getBankData();//中国银行
                getParitiesData();
            }

            @Override
            public void onLoadMore() {//加载监听
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        listView.loadMoreComplete();
                    }
                }, 1000);
            }
        });
        listView.setRefreshing(true);//代码控制下拉刷新
    }

    public void getParitiesData() {
        final String str1 = getCurrencyAcronym(spinner1.getSelectedItem().toString());
        final String str2 = getCurrencyAcronym(spinner2.getSelectedItem().toString());
        String str = DataBase.reanString(ExchangeRateActivity.this, str1 + str2);
        if (!TextUtils.isEmpty(str)) {
            str = str.replaceAll("\\\\n", "\n");
            str = str.replaceAll("\\\"", "");
            str = str.replaceAll("\\\\u003d", "=");
            content.setText(str);
        } else {
            content.setText("");
        }
        ((ExchangeRateApp) getApplication()).NetRequest(
                String.format("http://mhuilv.911cha.com/%s%s.html", str1, str2), 0,
               new Handler(){
                   public void handleMessage(android.os.Message msg) {//主线程接收消息
                       if (msg.obj != null) {
                                    Pattern p = Pattern.compile(
                                            "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*" +
                                                    "<p>(.+)</p>\\s*"
                                    );
                                    Matcher m = p.matcher(msg.obj.toString()); // 获取 matcher 对象
                                    if (m.find()) {
                                        String str = m.group(1) + "\n" +
                                                m.group(2) + "\n" +
                                                m.group(3) + "\n" +
                                                m.group(4) + "\n" +
                                                m.group(5) + "\n" +
                                                m.group(6) + "\n" +
                                                m.group(7) + "\n" +
                                                m.group(8);
                                        content.setText(str);
                                        DataBase.writeCurrencyBean(ExchangeRateActivity.this, str, str1 + str2);
                                    }
                                }
                                listView.refreshComplete();
                   }
                } ,
                "String");
    }
    //数据来自中国银行
    public void getBankData() {
        ((ExchangeRateApp) getApplication()).NetRequest(
                "http://www.boc.cn/sourcedb/whpj/", 0,
                new Handler(){
                    public void handleMessage(android.os.Message msg) {//主线程接收消息
                        if (msg.obj != null) {
                                    Pattern p = Pattern.compile(
                                            "<td>([\\u4E00-\\u9FFF]+)</td>\\s*" +//汉字至少一个      <用小括号把所有的值分组后遍历时可以group(i)取出所有值 >
                                                    "<td>(\\d+\\.?\\d*)?</td>\\s*" +//可能是没值的
                                                    "<td>(\\d+\\.?\\d*)?</td>\\s*" +
                                                    "<td>(\\d+\\.?\\d*)?</td>\\s*" +
                                                    "<td>(\\d+\\.?\\d*)?</td>\\s*" +
                                                    "<td>(\\d+\\.?\\d*)</td>\\s*" +//必需要有值
                                                    "<td>(20\\d{2}-\\d{2}-\\d{2})</td>\\s*" +//日期
                                                    "<td>(\\d{2}:\\d{2}:\\d{2})</td>\\s*"//时间
                                    );
                                    Matcher m = p.matcher(msg.obj.toString()); // 获取 matcher 对象
                                    datas.clear();
                                    while (m.find()) {                            //group(0)是整个未分割的串
                                        LinkedTreeMap<String, String> linkedTreeMap = new LinkedTreeMap<String, String>();
                                        linkedTreeMap.put("bank", "中国银行");
                                        linkedTreeMap.put("code", getCurrencyAcronym(m.group(1)));
                                        linkedTreeMap.put("currency", m.group(1));
                                        linkedTreeMap.put("buyPrice1", m.group(2));
                                        linkedTreeMap.put("buyPrice2", m.group(3));
                                        linkedTreeMap.put("sellPrice1", m.group(4));
                                        linkedTreeMap.put("sellPrice2", m.group(5));
                                        linkedTreeMap.put("cenPrice", m.group(6));
                                        linkedTreeMap.put("releasedate", m.group(7) + " " + m.group(8));
                                        datas.add(linkedTreeMap);
                                    }
                                    adapter.notifyDataSetChanged();
                                    DataBase.writeCurrencyBean(ExchangeRateActivity.this, datas, "bank.china");
                                } else {
                                    List<LinkedTreeMap<String, String>> xx = DataBase.reanCurrencyBean(ExchangeRateActivity.this, "bank.china");
                                    if (xx != null) {
                                        datas.clear();
                                        datas.addAll(xx);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getBaseContext(), "请注意当前信息发布时间,\n更新信息失败,请检查网络连接.", Toast.LENGTH_LONG).show();
                                }
                                listView.refreshComplete();
                    }
                } , "String");
    }

    //数据来自和讯银行
    public void getAllDate() {
        ((ExchangeRateApp) getApplication()).NetRequest(
                "http://data.bank.hexun.com/other/cms/foreignexchangejson.ashx?callback=ShowDatalist", 0,
                new Handler(){
                    public void handleMessage(android.os.Message msg) {//主线程接收消息
                        try {
                                    if (msg.obj != null) {
                                        String json = msg.obj.toString();
                                        json = json.substring(json.indexOf("["), json.lastIndexOf(")"));
                                        ArrayList<LinkedTreeMap<String, String>> xx = new Gson().fromJson(json, ArrayList.class);
                                        if (xx != null) {
                                            datas.clear();
                                            datas.addAll(xx);
                                            for (LinkedTreeMap<String, String> childData : datas) {
                                                if (TextUtils.isEmpty(childData.get("currency"))) {
                                                    datas.remove(childData);
                                                    break;
                                                }
                                            }
                                            Collections.sort(datas, new Comparator<LinkedTreeMap<String, String>>() {//按汇率大小排序
                                                @Override
                                                public int compare(LinkedTreeMap<String, String> o1, LinkedTreeMap<String, String> o2) {
                                                    if (TextUtils.isEmpty(o1.get("code"))) {
                                                        o1.put("code", getCurrencyAcronym(o1.get("currency")));
                                                    }
                                                    if (TextUtils.isEmpty(o2.get("code"))) {
                                                        o2.put("code", getCurrencyAcronym(o2.get("currency")));
                                                    }
                                                    return o1.get("code").compareTo(o2.get("code"));
                                                }
                                            });
                                            adapter.notifyDataSetChanged();
                                            DataBase.writeCurrencyBean(ExchangeRateActivity.this, datas, "bank.hexun");
                                        }
                                    } else {
                                        List<LinkedTreeMap<String, String>> xx = DataBase.reanCurrencyBean(ExchangeRateActivity.this, "bank.hexun");
                                        if (xx != null) {
                                            datas.clear();
                                            datas.addAll(xx);
                                        }
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getBaseContext(), "请注意当前信息发布时间,\n更新信息失败,请检查网络连接.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                listView.refreshComplete();
                    }
                }
      ,"String");
    }

    public String inspectString(String flag) {
        return TextUtils.isEmpty(flag) ? "未报价" : flag;
    }
    public String getCurrencyAcronym(String flag) {
        switch (flag) {
            case "冰岛克朗":
                return "ISK";
            case "波兰兹罗提":
                return "PLN";
            case "丹麦克朗":
                return "DKK";
            case "克罗地亚库纳":
                return "HRK";
            case "立陶宛立特":
                return "LTL";
            case "匈牙利福林":
                return "HUF";
            case "林吉特":
            case "马来西亚元":
                return "MYR";
            case "阿联酋 迪拉姆":
            case "阿联酋迪拉姆":
                return "AED";
            case "阿富汗尼":
                return "AFN";
            case "阿尔巴尼亚列克":
                return "ALL";
            case "安哥拉宽扎":
                return "AOA";
            case "阿根廷比索":
                return "ARS";
            case "澳大利亚元":
                return "AUD";
            case "阿鲁巴盾弗罗林":
                return "AWG";
            case "阿塞拜疆新马纳特":
                return "AZN";
            case "阿尔及利亚的丁那":
                return "DZD";
            case "埃及磅":
                return "EGP";
            case "埃塞俄比亚比尔":
                return "ETB";
            case "澳门元":
                return "MOP";
            case "阿曼里亚尔":
                return "OMR";
            case "安提瓜和巴布达岛东加勒比海元":
                return "XCU";
            case "波斯尼亚马尔卡":
                return "BAM";
            case "巴巴多斯元":
                return "BBD";
            case "保加利亚列弗":
                return "BGN";
            case "巴林第纳尔":
                return "BHD";
            case "BIF布隆迪法郎":
                return "BIF";
            case "百慕大美元":
                return "BMD";
            case "玻利维亚诺":
                return "BOB";
            case "巴西里亚尔":
            case "巴西雷亚尔":
                return "BRL";
            case "巴哈马元":
                return "BSD";
            case "不丹努扎姆":
                return "BTN";
            case "博茨瓦纳普拉":
                return "BWP";
            case "白俄罗斯卢布":
                return "BYR";
            case "伯利兹美元":
                return "BZD";
            case "巴拿马巴波亚":
                return "PAB";
            case "秘鲁索尔":
                return "PEN";
            case "巴布亚新几内亚":
                return "PGK";
            case "巴基斯坦卢比":
                return "PKR";
            case "巴拉圭瓜拉尼":
                return "PYG";
            case "朝鲜元":
                return "KPW";
            case "多明尼加比索":
                return "DOP";
            case "东加勒比元":
                return "XCD";
            case "厄立特里亚纳克法":
                return "ERN";
            case "卢布":
            case "俄罗斯卢布":
                return "RUB";
            case "佛得角埃斯库多":
                return "CVE";
            case "斐济元":
                return "FJD";
            case "福克兰镑":
                return "FKP";
            case "菲律宾比索":
                return "PHP";
            case "法属波利尼西亚法郎":
                return "XPF";
            case "刚果法郎":
                return "CDF";
            case "哥伦比亚比索":
                return "COP";
            case "哥斯达黎加科朗":
                return "CRC";
            case "古巴可兑换比索":
                return "CUC";
            case "古巴比索":
                return "CUP";
            case "格鲁吉亚拉里":
                return "GEL";
            case "冈比亚达拉西":
                return "GMD";
            case "圭亚那元":
                return "GYD";
            case "港币":
                return "HKD";
            case "国际货币基金组织特别提款权":
                return "XDR";
            case "荷属安的列斯盾":
                return "ANG";
            case "洪都拉斯伦皮拉":
                return "HNL";
            case "HTG海地古德":
                return "HTG";
            case "韩圆":
            case "韩国圆":
            case "韩国元":
            case "韩元":
            case "韩币":
                return "KRW";
            case "哈萨克斯坦 坚戈":
            case "哈萨克坚戈":
                return "KZT";
            case "加拿大元":
                return "CAD";
            case "加拿大元参考利率":
                return "CAX";
            case "捷克克朗":
                return "CZK";
            case "捷克基准利率":
                return "CZX";
            case "吉布提法郎":
                return "DJF";
            case "加纳塞地":
                return "GHS";
            case "几内亚法郎":
                return "GNF";
            case "吉尔吉斯斯坦索姆":
                return "KGS";
            case "柬埔寨瑞尔":
                return "KHR";
            case "津巴布韦元":
                return "ZWL";
            case "肯尼亚先令":
                return "KES";
            case "KMF科摩罗法郎":
                return "KMF";
            case "科威特丁那":
                return "KWD";
            case "开曼群岛元":
                return "KYD";
            case "卡塔尔利尔":
                return "QAR";
            case "老挝基普":
                return "LAK";
            case "黎巴嫩磅":
                return "LBP";
            case "利比里亚元":
                return "LRD";
            case "莱索托洛蒂":
                return "LSL";
            case "拉脱维亚拉特":
                return "LVL";
            case "利比亚第纳尔":
                return "LYD";
            case "罗马尼亚新列伊":
                return "RON";
            case "卢旺达法郎":
                return "RWF";
            case "美元":
                return "USD";
            case "孟加拉塔卡":
                return "BDT";
            case "MAD摩洛哥迪拉姆":
                return "MAD";
            case "MDL摩尔多瓦列伊":
                return "MDL";
            case "MGA马达加斯加阿里亚里":
                return "MGA";
            case "MKD马其顿第纳尔":
                return "MKD";
            case "MMK缅甸元":
                return "MMK";
            case "蒙古图格里克":
                return "MNT";
            case "毛里塔尼亚乌吉亚":
                return "MRO";
            case "毛里求斯卢比":
                return "MUR";
            case "马尔代夫拉菲亚":
                return "MVR";
            case "马拉维克瓦查":
                return "MWK";
            case "墨西哥比索":
                return "MXN";
            case "墨西哥衍生汇率":
                return "MXV";
            case "纳米比亚元":
                return "NAD";
            case "尼日利亚奈拉":
                return "NGN";
            case "尼加拉瓜新科多巴":
                return "NIO";
            case "挪威克朗":
                return "NOK";
            case "尼泊尔卢比":
                return "NPR";
            case "南非美分":
                return "ZAC";
            case "南非兰特":
                return "ZAR";
            case "欧元":
                return "EUR";
            case "欧元参考汇率":
                return "EUX";
            case "人民币":
                return "CNY";
            case "离岸人民币":
                return "CNH";
            case "瑞士法郎":
                return "CHF";
            case "日元":
                return "JPY";
            case "瑞典克朗":
                return "SEK";
            case "斯里兰卡卢比":
                return "LKR";
            case "塞尔维亚第纳尔":
                return "RSD";
            case "沙特里亚尔":
                return "SAR";
            case "所罗门群岛元":
                return "SBD";
            case "塞舌尔卢比":
                return "SCR";
            case "苏丹镑":
                return "SDG";
            case "圣圣赫勒拿镑":
                return "SHP";
            case "塞拉利昂利昂":
                return "SLL";
            case "索马里先令":
                return "SOS";
            case "苏里南元":
                return "SRD";
            case "圣多美多布拉":
                return "STD";
            case "萨尔瓦多科朗":
                return "SVC";
            case "斯威士兰里兰吉尼":
                return "SZL";
            case "泰国铢":
            case "泰铢":
                return "THB";
            case "索莫尼":
            case "塔吉克斯坦索莫尼":
                return "TJS";
            case "土库曼斯坦马纳特":
                return "TMT";
            case "突尼斯第纳尔":
                return "TND";
            case "汤加潘加":
                return "TOP";
            case "土耳其里拉":
                return "TRY";
            case "特立尼达多巴哥元":
                return "TTD";
            case "新台币":
            case "台币":
                return "TWD";
            case "坦桑尼亚先令":
                return "TZS";
            case "文莱元":
                return "BND";
            case "危地马拉格查尔":
                return "GTQ";
            case "乌克兰格里夫纳":
                return "UAH";
            case "乌干达先令":
                return "UGX";
            case "乌拉圭新比索":
                return "UYU";
            case "乌兹别克斯坦苏姆":
                return "UZS";
            case "委内瑞拉玻利瓦尔":
                return "VEF";
            case "瓦努阿图瓦图":
                return "VUV";
            case "新莫桑比克梅蒂卡尔":
                return "MZN";
            case "新西兰元":
                return "NZD";
            case "新加坡元":
                return "SGD";
            case "叙利亚磅":
                return "SYP";
            case "西萨摩亚塔拉":
                return "WST";
            case "西非法郎":
                return "XvOF";
            case "亚美尼亚德拉姆":
                return "AMD";
            case "英镑":
                return "GBP";
            case "印尼卢比":
            case "印度尼西亚盾":
                return "IDR";
            case "以色列阿高洛":
                return "ILA";
            case "以色列谢克尔":
                return "ILS";
            case "印度卢比":
                return "INR";
            case "伊拉克第纳尔":
                return "IQD";
            case "伊朗里亚尔":
                return "IRR";
            case "牙买加元":
                return "JMD";
            case "约旦第纳尔":
                return "JOD";
            case "越南盾":
                return "VND";
            case "也门里亚尔":
                return "YER";
            case "智利斯开法":
                return "CLF";
            case "智利比索":
                return "CLP";
            case "直布罗陀镑":
                return "GIP";
            case "中非金融合作法郎":
                return "XAF";
            case "赞比亚克瓦查":
                return "ZMW";
            default:
                return "";
        }
    }
}