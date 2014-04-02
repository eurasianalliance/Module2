#include <stdio.h>
#include <alt_types.h>
#include <altera_up_sd_card_avalon_interface.h>
#include "altera_up_avalon_video_pixel_buffer_dma.h"
#include "altera_up_avalon_video_character_buffer_with_dma.h"
#include "sys/alt_timestamp.h"
#include "sys/alt_alarm.h"
#include <sys/alt_cache.h>
#include <stdlib.h>
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "sys/alt_irq.h"

#include "sound_platform.h"

//-------------AUDIO FUNCTIONS---------------//
//-----------------------------------------------------------------------------------------------------------------
void av_config_setup()
{
    alt_up_av_config_dev * av_config =
            alt_up_av_config_open_dev("/dev/audio_video_config");
    if(av_config == NULL)
    {
        printf("Configuration failed\n");
    }
    while (!alt_up_av_config_read_ready(av_config)){}
    printf("Audio video configuration complete\n");
}
//---------------------------------------------------------------------------------------------------------------------------------------
void sdcardcheck(alt_up_sd_card_dev *device_reference)
{
    char file_name[20];
    int connected = 0;

    if (device_reference != NULL)
    {
        while(1)
        {
            if ((connected == 0) && (alt_up_sd_card_is_Present()))
            {
                //printf("Card connected.\n");
                if (alt_up_sd_card_is_FAT16())
                {
                    //printf("FAT16 file system detected.\n");
                    if(alt_up_sd_card_find_first(".", (char*) file_name)==0)
                    {
                        //printf("%s\n",file_name);
                        while(alt_up_sd_card_find_next((char*)file_name)==0)
                        {
                            //printf("%s\n", file_name);
                            alt_up_sd_card_read(file_name);
                        }
                    }
                    break;
                }
                else
                {
                    printf("Unknown file system.\n");
                }
                connected = 1;
            }
            else if ((connected == 1) && (alt_up_sd_card_is_Present() == false))
            {
                printf("Card disconnected.\n"); connected = 0;
            }
        }
    }
    else
    {
        printf("Card could not be opened\n");
    }
}

//---CLING---//
int read_audio(unsigned int *buffer, char* filename, short int handle)
{
    short int data;
    unsigned char upper;
    unsigned char lower;
    int size;

    alt_up_audio_dev *audio=alt_up_audio_open_dev("/dev/audio");

    //printf("Now loading: %s\n", filename);

    handle = alt_up_sd_card_fopen(filename, false);
    if (handle < 0) {
        printf("Failed to open the file");
        return;
    }

    int i;

    //printf("first for loop\n");
    for (i = 0; i < 46; i++) {
        alt_up_sd_card_read(handle);
    }
    size = 0;
    while (1) {

        //printf("inside the while loop\n");

        data = alt_up_sd_card_read(handle);
        //printf("data is 0x%x", data);

        if (data == -1) {
            break;
        } else {
            lower = data;
        }

        data = alt_up_sd_card_read(handle);
        if (data == -1) {
            break;
        } else {
            upper = data;
        }

        buffer[size] = ((upper << 8) | lower);
        //printf("buffer is 0x%x\n", buffer[size]);
        size++;
        if (size + 1 > CLING_L) {//7720 for cha ching sound
            break;
        }
        //play_audio(audio, buffer, handle);
    }

    //printf("Song size: %d\n", size);
    alt_up_sd_card_fclose(handle);

    return size;
}

void play_audio(alt_up_audio_dev *audio, unsigned int *buffer, short int handle)
{
    int lbytes;
    int rbytes;
    int lindex=22;
    int rindex=22;
    alt_up_audio_dev* aud=audio;
    int i=0;

        while(i<CLING_S) //200 for cha ching //this determines the size of the song that will be played
        {
             if (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) > 22)
             {
                lbytes = alt_up_audio_write_fifo(aud, &buffer[lindex], 96, ALT_UP_AUDIO_LEFT);
                rbytes = alt_up_audio_write_fifo(aud, &buffer[rindex], 96, ALT_UP_AUDIO_RIGHT);
                lindex += lbytes;
                rindex += rbytes;
                i++;
             }
        }
        //printf("all good things come to an end\n");
        alt_up_sd_card_fclose(handle);
}

//---CCHAT---//
int read_cchat(unsigned int *cchat_buf, char* cchat_name, short int cchat_handle)
{
    short int data;
    unsigned char upper;
    unsigned char lower;
    int size;

    alt_up_audio_dev *audio=alt_up_audio_open_dev("/dev/audio");

    //printf("Now loading: %s\n", filename);

    cchat_handle = alt_up_sd_card_fopen(cchat_name, false);
    if (cchat_handle < 0) {
        printf("Failed to open the file");
        return;
    }

    int i;

    //printf("first for loop\n");
    for (i = 0; i < 46; i++) {
        alt_up_sd_card_read(cchat_handle);
    }
    size = 0;
    while (1) {

        //printf("inside the while loop\n");

        data = alt_up_sd_card_read(cchat_handle);
        //printf("data is 0x%x", data);

        if (data == -1) {
            break;
        } else {
            lower = data;
        }

        data = alt_up_sd_card_read(cchat_handle);
        if (data == -1) {
            break;
        } else {
            upper = data;
        }

        cchat_buf[size] = ((upper << 8) | lower);
        //printf("buffer is 0x%x\n", buffer[size]);
        size++;
        if (size + 1 > CCHAT_L) {//7720 for cha ching sound
            break;
        }
        //play_audio(audio, buffer, handle);
    }

    //printf("Song size: %d\n", size);
    alt_up_sd_card_fclose(cchat_handle);

    return size;
}

void play_cchat(alt_up_audio_dev *audio, unsigned int *cchat_buf, short int cchat_handle)
{
    int lbytes;
    int rbytes;
    int lindex=22;
    int rindex=22;
    alt_up_audio_dev* aud=audio;
    int i=0;

        while(i<CCHAT_S) //200 for cha ching //this determines the size of the song that will be played
        {
             if (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) > 22)
             {
                lbytes = alt_up_audio_write_fifo(aud, &cchat_buf[lindex], 96, ALT_UP_AUDIO_LEFT);
                rbytes = alt_up_audio_write_fifo(aud, &cchat_buf[rindex], 96, ALT_UP_AUDIO_RIGHT);
                lindex += lbytes;
                rindex += rbytes;
                i++;
             }
        }
        //printf("all good things come to an end\n");
        alt_up_sd_card_fclose(cchat_handle);
}

//---PDEATH---//
int read_pdeath(unsigned int *pdeath_buf, char* pdeath_name, short int pdeath_handle)
{
    short int data;
    unsigned char upper;
    unsigned char lower;
    int size;

    alt_up_audio_dev *audio=alt_up_audio_open_dev("/dev/audio");

    //printf("Now loading: %s\n", filename);

    pdeath_handle = alt_up_sd_card_fopen(pdeath_name, false);
    if (pdeath_handle < 0) {
        printf("Failed to open the file");
        return;
    }

    int i;

    //printf("first for loop\n");
    for (i = 0; i < 46; i++) {
        alt_up_sd_card_read(pdeath_handle);
    }
    size = 0;
    while (1) {

        //printf("inside the while loop\n");

        data = alt_up_sd_card_read(pdeath_handle);
        //printf("data is 0x%x", data);

        if (data == -1) {
            break;
        } else {
            lower = data;
        }

        data = alt_up_sd_card_read(pdeath_handle);
        if (data == -1) {
            break;
        } else {
            upper = data;
        }

        pdeath_buf[size] = ((upper << 8) | lower);
        //printf("buffer is 0x%x\n", buffer[size]);
        size++;
        if (size + 1 > PDEATH_L) {//7720 for cha ching sound
            break;
        }
        //play_audio(audio, buffer, handle);
    }

    //printf("Song size: %d\n", size);
    alt_up_sd_card_fclose(pdeath_handle);

    return size;
}

void play_pdeath(alt_up_audio_dev *audio, unsigned int *pdeath_buf, short int pdeath_handle)
{
    int lbytes;
    int rbytes;
    int lindex=22;
    int rindex=22;
    alt_up_audio_dev* aud=audio;
    int i=0;

        while(i<PDEATH_S) //200 for cha ching //this determines the size of the song that will be played
        {
             if (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) > 22)
             {
                lbytes = alt_up_audio_write_fifo(aud, &pdeath_buf[lindex], 96, ALT_UP_AUDIO_LEFT);
                rbytes = alt_up_audio_write_fifo(aud, &pdeath_buf[rindex], 96, ALT_UP_AUDIO_RIGHT);
                lindex += lbytes;
                rindex += rbytes;
                i++;
             }
        }
        //printf("all good things come to an end\n");
        alt_up_sd_card_fclose(pdeath_handle);
}

//---PEW---//
int read_pew(unsigned int *pew_buf, char* pew_name, short int pew_handle)
{
    short int data;
    unsigned char upper;
    unsigned char lower;
    int size;

    alt_up_audio_dev *audio=alt_up_audio_open_dev("/dev/audio");

    //printf("Now loading: %s\n", filename);

    pew_handle = alt_up_sd_card_fopen(pew_name, false);
    if (pew_handle < 0) {
        printf("Failed to open the file");
        return;
    }

    int i;

    //printf("first for loop\n");
    for (i = 0; i < 46; i++) {
        alt_up_sd_card_read(pew_handle);
    }
    size = 0;
    while (1) {

        //printf("inside the while loop\n");

        data = alt_up_sd_card_read(pew_handle);
        //printf("data is 0x%x", data);

        if (data == -1) {
            break;
        } else {
            lower = data;
        }

        data = alt_up_sd_card_read(pew_handle);
        if (data == -1) {
            break;
        } else {
            upper = data;
        }

        pew_buf[size] = ((upper << 8) | lower);
        //printf("buffer is 0x%x\n", buffer[size]);
        size++;
        if (size + 1 > PEW_L) {//7720 for cha ching sound
            break;
        }
        //play_audio(audio, buffer, handle);
    }

    //printf("Song size: %d\n", size);
    alt_up_sd_card_fclose(pew_handle);

    return size;
}

void play_pew(alt_up_audio_dev *audio, unsigned int *pew_buf, short int pew_handle)
{
    int lbytes;
    int rbytes;
    int lindex=22;
    int rindex=22;
    alt_up_audio_dev* aud=audio;
    int i=0;

        while(i<PEW_S) //200 for cha ching //this determines the size of the song that will be played
        {
             if (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) > 22)
             {
                lbytes = alt_up_audio_write_fifo(aud, &pew_buf[lindex], 96, ALT_UP_AUDIO_LEFT);
                rbytes = alt_up_audio_write_fifo(aud, &pew_buf[rindex], 96, ALT_UP_AUDIO_RIGHT);
                lindex += lbytes;
                rindex += rbytes;
                i++;
             }
        }
        //printf("all good things come to an end\n");
        alt_up_sd_card_fclose(pew_handle);
}

//---WUBBLE---//
int read_wub(unsigned int *wub_buf, char* wub_name, short int wub_handle)
{
    short int data;
    unsigned char upper;
    unsigned char lower;
    int size;

    alt_up_audio_dev *audio=alt_up_audio_open_dev("/dev/audio");

    //printf("Now loading: %s\n", filename);

    wub_handle = alt_up_sd_card_fopen(wub_name, false);
    if (wub_handle < 0) {
        printf("Failed to open the file");
        return;
    }

    int i;

    //printf("first for loop\n");
    for (i = 0; i < 46; i++) {
        alt_up_sd_card_read(wub_handle);
    }
    size = 0;
    while (1) {

        //printf("inside the while loop\n");

        data = alt_up_sd_card_read(wub_handle);
        //printf("data is 0x%x", data);

        if (data == -1) {
            break;
        } else {
            lower = data;
        }

        data = alt_up_sd_card_read(wub_handle);
        if (data == -1) {
            break;
        } else {
            upper = data;
        }

        wub_buf[size] = ((upper << 8) | lower);
        //printf("buffer is 0x%x\n", buffer[size]);
        size++;
        if (size + 1 > WUBBLE_L) {//7720 for cha ching sound
            break;
        }
        //play_audio(audio, buffer, handle);
    }

    //printf("Song size: %d\n", size);
    alt_up_sd_card_fclose(wub_handle);

    return size;
}

void play_wub(alt_up_audio_dev *audio, unsigned int *wub_buf, short int wub_handle)
{
    int lbytes;
    int rbytes;
    int lindex=22;
    int rindex=22;
    alt_up_audio_dev* aud=audio;
    int i=0;

        while(i<WUBBLE_S) //200 for cha ching //this determines the size of the song that will be played
        {
             if (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) > 22)
             {
                lbytes = alt_up_audio_write_fifo(aud, &wub_buf[lindex], 96, ALT_UP_AUDIO_LEFT);
                rbytes = alt_up_audio_write_fifo(aud, &wub_buf[rindex], 96, ALT_UP_AUDIO_RIGHT);
                lindex += lbytes;
                rindex += rbytes;
                i++;
             }
        }
        //printf("all good things come to an end\n");
        alt_up_sd_card_fclose(wub_handle);
}
