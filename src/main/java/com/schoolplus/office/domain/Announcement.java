package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.AnnouncementChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "announcement_title")
    private String announcementTitle;

    @Column(name = "announcement_description", length = 5000)
    private String announcementDescription;

    @Column(name = "announcement_status")
    private Boolean announcementStatus;

    @ElementCollection
    @CollectionTable(name = "announcement_channels", joinColumns = @JoinColumn(name = "announcement_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_name")
    private List<AnnouncementChannel> announcementChannels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "announcement_images",
            joinColumns = @JoinColumn(name = "announcement_id"))
    @CollectionId(
            columns = @Column(name = "image_id"),
            type = @Type(type = "long"),
            generator = "sequence")
    @Column(name = "imageUrl")
    private List<AnnouncementImage> announcementImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addImage(AnnouncementImage announcementImage) {
        if (!announcementImages.contains(announcementImage)) {
            this.announcementImages.add(announcementImage);
        }
    }

    public void removeImage(AnnouncementImage announcementImage) {
        if (announcementImages.contains(announcementImage)) {
            this.announcementImages.remove(announcementImage);
        }
    }

    public void addChannel(AnnouncementChannel announcementChannel) {
        if (!announcementChannels.contains(announcementChannel)) {
            this.announcementChannels.add(announcementChannel);
        }
    }

    public void removeChannel(AnnouncementChannel announcementChannel) {
        if (announcementChannels.contains(announcementChannel)) {
            this.announcementChannels.remove(announcementChannel);
        }
    }

}
