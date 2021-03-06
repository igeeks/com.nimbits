/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * @fileoverview Regression test based on some strange customBars data.
 * @author danvk@google.com (Dan Vanderkam)
 */
var CustomBarsTestCase = TestCase("custom-bars");

CustomBarsTestCase.prototype.setUp = function() {
  document.body.innerHTML = "<div id='graph'></div>";
};

CustomBarsTestCase.prototype.tearDown = function() {
};

// This test used to reliably produce an infinite loop.
CustomBarsTestCase.prototype.testCustomBarsNoHang = function() {
  var opts = {
    width: 480,
    height: 320,
    customBars: true
  };
  var data = "X,Y1,Y2\n" +
    "1,1178.0;1527.5;1856.6,0;22365658;0\n" +
    "2,1253.0;1303.3;1327.3,0;22368228;0\n" +
    "3,878.0;1267.0;1357.1,0;22368895;0\n" +
    "4,1155.0;1273.1;1303.5,0;22369665;0\n" +
    "5,1089.0;1294.8;1355.3,0;22370160;0\n" +
    "6,1088.0;1268.6;1336.1,0;22372346;0\n" +
    "7,1141.0;1269.1;1301.2,0;22373318;0\n" +
    "8,1072.0;1255.8;1326.2,0;22374310;0\n" +
    "9,1209.0;1309.2;1351.8,0;22374924;0\n" +
    "10,1230.0;1303.9;1332.6,0;22380163;0\n" +
    "11,1014.0;1263.5;1330.8,0;22381117;0\n" +
    "12,853.0;1215.6;1330.6,0;22381556;0\n" +
    "13,1134.0;1581.9;1690.1,0;22384631;0\n" +
    "14,1113.0;1540.1;1676.5,0;22386933;0\n" +
    "15,1130.0;1542.7;1678.3,0;22393459;0\n" +
    "18,1582.0;1644.4;1690.2,0;22395914;0\n" +
    "19,878.0;1558.3;1708.1,0;22397732;0\n" +
    "20,1076.0;1598.4;1723.8,0;22397886;0\n" +
    "21,1077.0;1574.0;1685.3,0;22398659;0\n" +
    "22,1118.0;1590.4;1697.6,0;22399009;0\n" +
    "23,1031.0;1473.1;1644.9,0;22401969;0\n" +
    "24,1090.0;1480.7;1640.0,0;22417989;0\n" +
    "25,1592.0;1681.7;1714.4,0;22422819;0\n" +
    "26,1251.0;1657.8;1750.6,0;22423681;0\n" +
    "27,1144.0;1660.9;1776.2,0;22426947;0\n" +
    "28,1178.0;1642.4;1745.6,0;22428238;0\n" +
    "29,1169.0;1649.1;1757.5,0;22429524;0\n" +
    "30,1150.0;1596.1;1746.7,0;22433472;0\n" +
    "31,1099.0;1586.5;1732.8,0;22434308;0\n" +
    "32,1120.0;1456.0;1620.3,0;22434821;0\n" +
    "33,1640.0;1687.7;1709.0,0;22434882;0\n" +
    "34,1671.0;1712.1;1733.7,0;22435116;0\n" +
    "35,,0;22437620;0\n";
  var graph = document.getElementById("graph");
  var g = new Dygraph(graph, data, opts);
};
