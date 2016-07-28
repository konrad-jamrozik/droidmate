# To plot data from var_data_file_path to a file var_output_file_path, run this script as follows:
# gnuplot -e "var_interactive=0" plot_template.plt

if (!exists("var_interactive")) var_interactive=1
if (!exists("var_data_file_path")) var_data_file_path="plot_data.txt"
if (!exists("var_output_file_path")) var_output_file_path="plot.pdf"

if (var_interactive) \
  set terminal wxt; \
  set output ; \
else \
  set output var_output_file_path; \
  set terminal pdf size 3.5,2.62 color


# Data points equal to -1 denote "data point missing". 
# This command stops missing data points from being plotted.
set datafile missing "-1"

# Prevent underscores in column titles to be treated as underscript
set termoption noenhanced

# Line width of the axes
set border linewidth 1

# Legend on the top-left
set key left top

# Causes the first entry in each column of input data to be interpreted as a text string and used as a title for the corresponding plot
set key autotitle columnheader

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
plot var_data_file_path using 1:2 with lines linestyle 1,\
     var_data_file_path using 1:3 with lines linestyle 2
     
unset output
reset
