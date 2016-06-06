# To plot data from "${var_data_file_name}" to a file "${var_data_file_name}.pdf", run this script as follows:
# gnuplot -e "var_interactive=0" plot_template.plt

if (!exists("var_interactive")) var_interactive=1
if (!exists("var_data_file_name")) var_data_file_name="plot_data.txt"
if (!exists("var_output_file_name")) var_output_file_name="plot"

if (var_interactive) {
  set terminal wxt
  set output
} else {
  set output var_output_file_name.'.pdf'
  set terminal pdf size 3.5,2.62 color
}

set datafile missing "-1"

# Prevent underscores in column titles to be treated as underscript
set termoption noenhanced

# Line width of the axes
set border linewidth 1

# Legend
# set key at 6.1,1.3
set key autotitle columnhead

# Axes label
# set xlabel 'seconds'
# set ylabel 'count'

# Axes ranges
#set xrange [*:*]
#set yrange [0:20]
set offsets 1, 1, 1, 1

# Axes tics
#set xtics 1
#set ytics 1
set tics scale 0.75

# Line styles
# blue
set style line 1 linecolor rgb '#0060ad' linetype 1 linewidth 2 pointtype 7 pointsize 0.1 
# red
set style line 2 linecolor rgb '#dd181f' linetype 1 linewidth 2 pointtype 7 pointsize 0.1

# Plot
plot var_data_file_name using 1:2 with points linestyle 1,\
     var_data_file_name using 1:3 with points linestyle 2
     
unset output
reset