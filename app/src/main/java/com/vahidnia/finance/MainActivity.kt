package com.vahidnia.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

data class Tx(val title:String,val amount:Long,val income:Boolean)
data class Work(val place:String,val from:String,val to:String,val note:String)

class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{App()}}}

@Composable fun App(){
 var page by remember{mutableIntStateOf(0)}
 var txs by remember{mutableStateOf(emptyList<Tx>())}
 var works by remember{mutableStateOf(emptyList<Work>())}
 Scaffold(bottomBar={NavigationBar{listOf(Icons.Default.Home to "خانه",Icons.Default.AccountBalanceWallet to "مالی",Icons.Default.Work to "کار",Icons.Default.BarChart to "گزارش").forEachIndexed{i,p->NavigationBarItem(page==i,{page=i},{Icon(p.first,null)},label={Text(p.second)})}}}){pad->CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl){Box(Modifier.padding(pad)){when(page){0->Home(txs,works);1->Finance(txs){txs=it};2->WorkPage(works){works=it};3->Report(txs,works)}}}}
}
fun money(n:Long)=NumberFormat.getNumberInstance(Locale("fa","IR")).format(n)+" تومان"
@Composable fun Home(t:List<Tx>,w:List<Work>){val i=t.filter{it.income}.sumOf{it.amount};val e=t.filter{!it.income}.sumOf{it.amount};Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("مدیریت مالی وحیدینیا",style=MaterialTheme.typography.headlineSmall);Card(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("موجودی");Text(money(i-e),style=MaterialTheme.typography.headlineMedium)}};Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Card(Modifier.weight(1f)){Column(Modifier.padding(14.dp)){Text("درآمد");Text(money(i))}};Card(Modifier.weight(1f)){Column(Modifier.padding(14.dp)){Text("هزینه");Text(money(e))}}};Card(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("روزهای کاری");Text(w.size.toString(),style=MaterialTheme.typography.headlineMedium)}}}}
@Composable fun Finance(t:List<Tx>,set:(List<Tx>)->Unit){var show by remember{mutableStateOf(false)};var title by remember{mutableStateOf("")};var amount by remember{mutableStateOf("")};var inc by remember{mutableStateOf(true)};Column(Modifier.fillMaxSize().padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text("مدیریت مالی",style=MaterialTheme.typography.headlineSmall);Button({show=true}){Text("افزودن")}};LazyColumn{items(t.reversed()){x->ListItem({Text(x.title)},supportingContent={Text(if(x.income)"درآمد" else "هزینه")},trailingContent={Text(money(if(x.income)x.amount else -x.amount))})}}};if(show)AlertDialog({show=false},{},{title={Text("ثبت تراکنش")},text={Column{OutlinedTextField(title,{title=it},label={Text("عنوان")});OutlinedTextField(amount,{amount=it},label={Text("مبلغ")});Row{RadioButton(inc,{inc=true});Text("درآمد");RadioButton(!inc,{inc=false});Text("هزینه")}}},confirmButton={Button({amount.toLongOrNull()?.let{set(t+Tx(title,it,inc));show=false}}){Text("ذخیره")}},dismissButton={TextButton({show=false}){Text("لغو")}}})}
@Composable fun WorkPage(w:List<Work>,set:(List<Work>)->Unit){var show by remember{mutableStateOf(false)};var place by remember{mutableStateOf("")};var from by remember{mutableStateOf("")};var to by remember{mutableStateOf("")};var note by remember{mutableStateOf("")};Column(Modifier.fillMaxSize().padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("روزهای کاری",style=MaterialTheme.typography.headlineSmall);Button({show=true}){Text("ثبت روز")}};LazyColumn{items(w.reversed()){x->ListItem({Text(x.place)},supportingContent={Text("${x.from} تا ${x.to} ${x.note}")})}}};if(show)AlertDialog({show=false},{},{title={Text("ثبت روز کاری")},text={Column{OutlinedTextField(place,{place=it},label={Text("کجا رفتم؟")});OutlinedTextField(from,{from=it},label={Text("ساعت شروع")});OutlinedTextField(to,{to=it},label={Text("ساعت پایان")});OutlinedTextField(note,{note=it},label={Text("یادداشت")})}},confirmButton={Button({set(w+Work(place,from,to,note));show=false}){Text("ذخیره")}},dismissButton={TextButton({show=false}){Text("لغو")}}})}
@Composable fun Report(t:List<Tx>,w:List<Work>){val i=t.filter{it.income}.sumOf{it.amount};val e=t.filter{!it.income}.sumOf{it.amount};Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("گزارش‌ها",style=MaterialTheme.typography.headlineSmall);Text("مجموع درآمد: ${money(i)}");Text("مجموع هزینه: ${money(e)}");Text("خالص: ${money(i-e)}");Text("تعداد روز کاری: ${w.size}")}}
