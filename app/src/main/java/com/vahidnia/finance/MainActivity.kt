package com.vahidnia.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.IntOffset
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
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
data class BankCard(val bank:String,val number:String,val balance:Long,val color:Color)

class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{App()}}}

@Composable fun App(){
 var page by remember{mutableIntStateOf(0)}
 var txs by remember{mutableStateOf(emptyList<Tx>())}
 var works by remember{mutableStateOf(emptyList<Work>())}
 var rtl by remember{mutableStateOf(true)}
 val direction=if(rtl) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr
 CompositionLocalProvider(LocalLayoutDirection provides direction){
  Scaffold(
   containerColor=MaterialTheme.colorScheme.background,
   topBar={TopAppBar(title={Text(when(page){0->"مدیریت مالی وحیدینیا";1->"مدیریت مالی";2->"روزهای کاری";else->"گزارش‌ها"},fontWeight=FontWeight.SemiBold)},actions={
    IconButton(onClick={rtl=!rtl}){Icon(if(rtl)Icons.Default.FormatTextdirectionRToL else Icons.Default.FormatTextdirectionLToR,"تغییر جهت")}
    IconButton(onClick={}){Icon(Icons.Default.Settings,"تنظیمات")}
   })},
   bottomBar={NavigationBar{listOf(Icons.Default.Home to "خانه",Icons.Default.AccountBalanceWallet to "مالی",Icons.Default.Work to "کار",Icons.Default.BarChart to "گزارش").forEachIndexed{i,p->NavigationBarItem(page==i,{page=i},{Icon(p.first,null)},label={Text(p.second)})}}}
  ){pad->
   Box(Modifier.fillMaxSize().padding(pad)){
    AnimatedContent(targetState=page,transitionSpec={fadeIn(tween(180)) togetherWith fadeOut(tween(120))},label="page"){
     when(it){0->Home(txs,works);1->Finance(txs){txs=it};2->WorkPage(works){works=it};3->Report(txs,works)}
    }
   }
  }
 }
}
fun money(n:Long)=NumberFormat.getNumberInstance(Locale("fa","IR")).format(n)+" تومان"
@Composable fun Home(t:List<Tx>,w:List<Work>){
 val i=t.filter{it.income}.sumOf{it.amount};val e=t.filter{!it.income}.sumOf{it.amount}
 val cards=listOf(
  BankCard("بانک ملی","6037 •••• •••• ۱۲۳۴",i-e,Color(0xFF1769AA)),
  BankCard("بانک مسکن","6280 •••• •••• ۵۶۷۸",0,Color(0xFF00897B)),
  BankCard("بلوبانک","6219 •••• •••• ۹۱۰۱",0,Color(0xFF3155D8)),
  BankCard("رد بانک","5022 •••• •••• ۲۳۴۵",0,Color(0xFFE53935)),
  BankCard("بانک مهر","6063 •••• •••• ۶۷۸۹",0,Color(0xFF7B1FA2))
 )
 LazyColumn(Modifier.fillMaxSize().padding(horizontal=16.dp),contentPadding=PaddingValues(top=8.dp,bottom=24.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  item{Text("کارت‌های بانکی",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)}
  item{LazyRow(horizontalArrangement=Arrangement.spacedBy(12.dp),contentPadding=PaddingValues(end=4.dp)){items(cards){card->
   Card(shape=RoundedCornerShape(24.dp),modifier=Modifier.width(285.dp).height(170.dp),colors=CardDefaults.cardColors(containerColor=card.color)){
    Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.SpaceBetween){
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
      Text(card.bank,color=Color.White,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
      Icon(Icons.Default.CreditCard,null,tint=Color.White)
     }
     Column{Text(card.number,color=Color.White.copy(alpha=.9f),style=MaterialTheme.typography.titleMedium);Spacer(Modifier.height(8.dp));Text(money(card.balance),color=Color.White,fontWeight=FontWeight.Bold)}
    }
   }
  }}}
  item{Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)){Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text("موجودی کل",style=MaterialTheme.typography.labelLarge);Text(money(i-e),style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)}}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Card(Modifier.weight(1f)){Column(Modifier.padding(14.dp)){Text("درآمد");Text(money(i),fontWeight=FontWeight.Bold)}};Card(Modifier.weight(1f)){Column(Modifier.padding(14.dp)){Text("هزینه");Text(money(e),fontWeight=FontWeight.Bold)}}}}
  item{Card(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("روزهای کاری");Text(w.size.toString(),style=MaterialTheme.typography.headlineMedium)}}}
 }
}

@Composable fun Finance(t:List<Tx>,set:(List<Tx>)->Unit){
 var show by remember{mutableStateOf(false)};var title by remember{mutableStateOf("")};var amount by remember{mutableStateOf("")};var inc by remember{mutableStateOf(true)}
 Column(Modifier.fillMaxSize().padding(horizontal=16.dp)){
  Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
   Column{Text("تراکنش‌ها",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Text("${t.size} مورد ثبت شده",style=MaterialTheme.typography.bodySmall)}
   FilledTonalButton({show=true},shape=RoundedCornerShape(14.dp)){Icon(Icons.Default.Add,null);Spacer(Modifier.width(6.dp));Text("افزودن")}
  }
  LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=24.dp)){
   items(t.reversed()){x->
    SwipeDeleteRow(onDelete={set(t.toMutableList().also{it.remove(x)})}){
     Card(shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()){
      ListItem(headlineContent={Text(x.title.ifBlank{"بدون عنوان"},fontWeight=FontWeight.Medium)},supportingContent={Text(if(x.income)"درآمد" else "هزینه")},leadingContent={Surface(shape=RoundedCornerShape(14.dp),color=if(x.income)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer){Icon(if(x.income)Icons.Default.TrendingUp else Icons.Default.TrendingDown,null,Modifier.padding(10.dp))}},trailingContent={Text((if(x.income)"+" else "-")+money(x.amount),fontWeight=FontWeight.Bold)})
     }
    }
   }
  }
 }
 if(show)AlertDialog(onDismissRequest={show=false},title={Text("ثبت تراکنش جدید",fontWeight=FontWeight.Bold)},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(title,{title=it},Modifier.fillMaxWidth(),label={Text("عنوان")},singleLine=true);OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},Modifier.fillMaxWidth(),label={Text("مبلغ")},singleLine=true);Row(verticalAlignment=Alignment.CenterVertically){RadioButton(inc,{inc=true});Text("درآمد");RadioButton(!inc,{inc=false});Text("هزینه")}}},confirmButton={Button({amount.toLongOrNull()?.let{set(t+Tx(title,it,inc));show=false}}){Text("ذخیره")}},dismissButton={TextButton({show=false}){Text("لغو")}})
}

@Composable fun SwipeDeleteRow(content:@Composable()->Unit,onDelete:()->Unit){
 var offset by remember{mutableFloatStateOf(0f)};var deleting by remember{mutableStateOf(false)}
 val rtl=LocalLayoutDirection.current==androidx.compose.ui.unit.LayoutDirection.Rtl
 Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))){
  Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.errorContainer),contentAlignment=if(rtl)Alignment.CenterStart else Alignment.CenterEnd){Row(Modifier.padding(horizontal=18.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Delete,null);Spacer(Modifier.width(6.dp));Text("حذف",fontWeight=FontWeight.Bold)}}
  Box(Modifier.fillMaxWidth().offset{IntOffset(offset.roundToInt(),0)}.background(MaterialTheme.colorScheme.surface).pointerInput(rtl){awaitPointerEventScope{while(true){val down=awaitPointerEvent().changes.firstOrNull()?:continue;if(!down.pressed)continue;var last=down.position.x;while(down.pressed){val e=awaitPointerEvent();val c=e.changes.firstOrNull()?:break;val dx=c.position.x-last;last=c.position.x;val logical=if(rtl)dx else -dx;offset=(offset+logical).coerceIn(-220f,220f);c.consume();if(!c.pressed)break};if(abs(offset)>130f){deleting=true;offset=if(rtl)220f else -220f}else offset=0f}}}}){content()}
 }
 if(deleting)LaunchedEffect(Unit){onDelete()}
}
@Composable fun WorkPage(w:List<Work>,set:(List<Work>)->Unit){var show by remember{mutableStateOf(false)};var place by remember{mutableStateOf("")};var from by remember{mutableStateOf("")};var to by remember{mutableStateOf("")};var note by remember{mutableStateOf("")};Column(Modifier.fillMaxSize().padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("روزهای کاری",style=MaterialTheme.typography.headlineSmall);Button({show=true}){Text("ثبت روز")}};LazyColumn{items(w.reversed()){x->ListItem({Text(x.place)},supportingContent={Text("${x.from} تا ${x.to} ${x.note}")})}}};if(show)AlertDialog({show=false},{},{title={Text("ثبت روز کاری")},text={Column{OutlinedTextField(place,{place=it},label={Text("کجا رفتم؟")});OutlinedTextField(from,{from=it},label={Text("ساعت شروع")});OutlinedTextField(to,{to=it},label={Text("ساعت پایان")});OutlinedTextField(note,{note=it},label={Text("یادداشت")})}},confirmButton={Button({set(w+Work(place,from,to,note));show=false}){Text("ذخیره")}},dismissButton={TextButton({show=false}){Text("لغو")}}})}
@Composable fun Report(t:List<Tx>,w:List<Work>){val i=t.filter{it.income}.sumOf{it.amount};val e=t.filter{!it.income}.sumOf{it.amount};Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("گزارش‌ها",style=MaterialTheme.typography.headlineSmall);Text("مجموع درآمد: ${money(i)}");Text("مجموع هزینه: ${money(e)}");Text("خالص: ${money(i-e)}");Text("تعداد روز کاری: ${w.size}")}}
