package com.flyingeffects.com.enity;

public class JadeTypeFace {

    private int id;
    private String title;
    private int create_time;
    private int status;
    private String icon_image;
    private int sort;
    private DetailBean detail;
    private boolean selected;


    public static class DetailBean {
        private InBrightBean in_bright;
        private ReliefBean relief;
        private Font3DBean font_3D;
        private String color;
        private String font;
        private String in_color;


        public InBrightBean getIn_bright() {
            return in_bright;
        }

        public void setIn_bright(InBrightBean in_bright) {
            this.in_bright = in_bright;
        }

        public ReliefBean getRelief() {
            return relief;
        }

        public void setRelief(ReliefBean relief) {
            this.relief = relief;
        }

        public Font3DBean getFont_3D() {
            return font_3D;
        }

        public void setFont_3D(Font3DBean font_3D) {
            this.font_3D = font_3D;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getFont() {
            return font;
        }

        public void setFont(String font) {
            this.font = font;
        }

        public String getIn_color() {
            return in_color;
        }

        public void setIn_color(String in_color) {
            this.in_color = in_color;
        }

        public static class InBrightBean {
            private String bright_color;
            private String fuzzy_radius;
            private String horizontal_shift;
            private String vertical_offset;

            public String getBright_color() {
                return bright_color;
            }

            public void setBright_color(String bright_color) {
                this.bright_color = bright_color;
            }

            public String getFuzzy_radius() {
                return fuzzy_radius;
            }

            public void setFuzzy_radius(String fuzzy_radius) {
                this.fuzzy_radius = fuzzy_radius;
            }

            public String getHorizontal_shift() {
                return horizontal_shift;
            }

            public void setHorizontal_shift(String horizontal_shift) {
                this.horizontal_shift = horizontal_shift;
            }

            public String getVertical_offset() {
                return vertical_offset;
            }

            public void setVertical_offset(String vertical_offset) {
                this.vertical_offset = vertical_offset;
            }
        }

        public static class ReliefBean {
            private String illumination_angle;
            private String illumination_intensity;
            private String oblique_angle;

            public String getIllumination_angle() {
                return illumination_angle;
            }

            public void setIllumination_angle(String illumination_angle) {
                this.illumination_angle = illumination_angle;
            }

            public String getIllumination_intensity() {
                return illumination_intensity;
            }

            public void setIllumination_intensity(String illumination_intensity) {
                this.illumination_intensity = illumination_intensity;
            }

            public String getOblique_angle() {
                return oblique_angle;
            }

            public void setOblique_angle(String oblique_angle) {
                this.oblique_angle = oblique_angle;
            }
        }

        public static class Font3DBean {
            private String angle;
            private String depth;
            private String color;

            public String getAngle() {
                return angle;
            }

            public void setAngle(String angle) {
                this.angle = angle;
            }

            public String getDepth() {
                return depth;
            }

            public void setDepth(String depth) {
                this.depth = depth;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIcon_image() {
        return icon_image;
    }

    public void setIcon_image(String icon_image) {
        this.icon_image = icon_image;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
